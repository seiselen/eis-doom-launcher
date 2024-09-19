package app;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import PrEis.utils.Cons;
import PrEis.utils.Cons.Err;
import PrEis.utils.FileSysUtils;
import PrEis.utils.FormatUtils;
import processing.data.JSONObject;

public class ConfigBuilder {

  enum IwadType {DOOM1,DOOM2,PLUT,TNT,ERR,NOSPEC};

  enum SpecFlag {ALT_LEV_WAD};

  private static boolean notNullish(String s){return s!=null && !s.isEmpty();}

  public static LoadConfig[] build(AppUtils au, String dirName){
    String dirPath = FileSysUtils.pathConcat(au.getFilepath(EResPath.DP_WADS),dirName);
    String[] _loadinfoFilename = new String[1];
    String loadinfoFilename;
    
    //> Search for `loadinfo.json`, assigning its filename to `loadinfoFilename` if found
    try {Files.find(Paths.get(dirPath), 1, (p,a)->(p.getFileName().toString().equals("loadinfo.json"))).forEach(path -> _loadinfoFilename[0]=path.toString());}
    catch(IOException ie){ie.printStackTrace(); return null;}
    loadinfoFilename = _loadinfoFilename[0];
    
    //> `loadinfo.json` was NOT found: assume there is none, ergo handle accordingly
    if(loadinfoFilename==null){
      String fname = au.getFilenameFromDir(dirPath);
      return FormatUtils.arr1FromObj(
        new LoadConfig(au)
        .setVal(fname)
        .setLbl(dirName)
        .setFilepath_WAD(fname)
        .setFilepath_BRIT(false)
      );
    }
  
    //> Load `loadinfo.json` contents, conserr'ing and returning null if (somehow) null
    JSONObject loadInfObJSON = au.app.loadJSONObject(loadinfoFilename);
    if(loadInfObJSON==null){Cons.err(Err.NULL_VALUE); return null;}
    
    //> Parse-&-Process spec for each possible component of `loadinfo`
    String[] wadSpec  = loadinfo_parseWAD(au, loadInfObJSON,dirPath);
    IwadType iwadSpec = loadinfo_parseIWAD(au, loadInfObJSON);
    String   dehSpec  = loadinfo_parseDeh(loadInfObJSON, wadSpec, dirPath);
    String   bexSpec  = loadinfo_parseBex(loadInfObJSON, wadSpec, dirPath);
    boolean  britspec = loadinfo_parseBMaps(loadInfObJSON);
    String   gwadspec = loadinfo_parseGWAD(loadInfObJSON);
    SpecFlag flagSpec = loadinfo_parseFlags(loadInfObJSON);
  
    //> Construct LoadConfig Object[s]
    if(wadSpec.length==1){
      return FormatUtils.arr1FromObj(new LoadConfig(au)
      .setVal(wadSpec[0])
      .setLbl(dirName)
      .setFilepath_WAD(wadSpec[0])
      .setFilepath_DEH(dehSpec)
      .setFilepath_BEX(bexSpec)
      .setFilepath_IWAD(iwadFilepathWithType(au, iwadSpec))
      .setFilepath_BRIT(britspec)
      .setFilepath_GWAD(gwadspec));
    }
    else if(wadSpec.length>1){
      //> handles special `ALT_LEV_WAD` case s.t. both wad and level+wad are spec'd
      if(flagSpec==SpecFlag.ALT_LEV_WAD){
        return new LoadConfig[]{
          /* primary wad */  new LoadConfig(au)
          .setVal(wadSpec[0])
          .setLbl(dirName)
          .setFilepath_WAD(wadSpec[0])
          .setFilepath_DEH(dehSpec)
          .setFilepath_IWAD(iwadFilepathWithType(au,iwadSpec))
          .setFilepath_BRIT(britspec)
          .setFilepath_GWAD(gwadspec),  
          /* spec-lev wad */ new LoadConfig(au)
          .setVal(wadSpec[1])
          .setLbl(dirName)
          .setFilepath_WAD(wadSpec[0]+" "+wadSpec[1])
          .setFilepath_DEH(dehSpec)
          .setFilepath_IWAD(iwadFilepathWithType(au,iwadSpec))
          .setFilepath_BRIT(britspec)
          .setFilepath_GWAD(gwadspec)
        };
      }
      //> handles multi-wad spec
      else {
        LoadConfig[] ret = new LoadConfig[wadSpec.length];
        for (int i=0; i<wadSpec.length; i++){
          ret[i] = new LoadConfig(au)
          .setVal(wadSpec[i])
          .setLbl(dirName)
          .setFilepath_WAD(wadSpec[i])
          .setFilepath_DEH(dehSpec)
          .setFilepath_IWAD(iwadFilepathWithType(au,iwadSpec))
          .setFilepath_BRIT(britspec)
          .setFilepath_GWAD(gwadspec);
        }
        return ret;
      }
    }
    return null;
  }

  private static IwadType iwadTypeWithString(String s){
    if(s==null){return IwadType.NOSPEC;}
    if(s.equals("doom")){return IwadType.DOOM1;}
    if(s.equals("doom2")){return IwadType.DOOM2;}
    if(s.equals("plut")){return IwadType.PLUT;}
    if(s.equals("tnt")){return IwadType.TNT;}
    return IwadType.ERR;
  }
  
  private static String iwadFilepathWithType(AppUtils au, IwadType t){
    switch(t){
      case DOOM1: return au.getFilepath(EResPath.FP_DOOM);
      case DOOM2: return au.getFilepath(EResPath.FP_DOOM2);
      case  PLUT: return au.getFilepath(EResPath.FP_PLUT);
      case   TNT: return au.getFilepath(EResPath.FP_TNT);
      //> {ERR,NOSPEC} return null, which inevitably resolves `doom2` as IWAD
      default: return null;
    }
  }

  private static String[] loadinfo_parseWAD(AppUtils au, JSONObject jObj, String wadDir){
    //> Handles explicit single-wad specification
    try {if(jObj.getString("wad")!=null && !jObj.getString("wad").isEmpty()){return new String[]{
      FileSysUtils.pathConcat(wadDir,jObj.getString("wad"))};}} catch (Exception e){;}
    //> Handles implicit multi-wad specification
    try {if(jObj.getBoolean("wad")){return au.getWADFilenamesFromDir(wadDir);}} catch (Exception e){;}
    //> Handles explicit multi-wad specification (BOTH standard and for use with `ALT_LEV_WAD` flag)
    try {if(jObj.getJSONArray("wad")!=null){
      String[] jArr=jObj.getJSONArray("wad").toStringArray();
      for (int i=0; i<jArr.length; i++) {
        jArr[i]=FileSysUtils.pathConcat(wadDir,jArr[i]);
      } return jArr;}
    } catch (Exception e){;}
    //> Handles no wad specification
    return FormatUtils.arr1FromObj(au.getFilenameFromDir(wadDir));
  }

  private static IwadType loadinfo_parseIWAD(AppUtils au, JSONObject jObj){
    //> Attempt to load spec'd IWAD. If it exists and is valid: define only its Iwadtype (for now); else null
    String spec_iwad = jObj.getString("iwad");
    if(spec_iwad==null){return IwadType.NOSPEC;}
    return iwadTypeWithString(spec_iwad);
  }

  private static String loadinfo_parseDeh(JSONObject jObj, String[] parsedWAD, String wadDir){
    //> Handles implicit specification
    try {
      if(jObj.getBoolean("deh")){
        return parsedWAD[0].substring(0,parsedWAD[0].length()-3)+"deh";
      }
    } catch (Exception e){;}
    //> Handles explicit specification
    try {
      if(jObj.getString("deh")!=null && !jObj.getString("deh").isEmpty()){
        return FileSysUtils.pathConcat(wadDir,jObj.getString("deh")+".deh");
      }
    } catch (Exception e){;}
    //> Handles no specification
    return null;
  }

  private static String loadinfo_parseBex(JSONObject jObj, String[] parsedWAD, String wadDir){
    //> Handles implicit specification
    try {
      if(jObj.getBoolean("bex")){return parsedWAD[0].substring(0,parsedWAD[0].length()-3)+"bex";}
    }
    catch (Exception e){;}
    //> Handles explicit specification
    try {
      String s = jObj.getString("bex");
      if(notNullish(s)){return FileSysUtils.pathConcat(wadDir,s+".bex");}
    } catch (Exception e){;}
    //> Handles no specification
    return null;
  }

  private static boolean loadinfo_parseBMaps(JSONObject jObj){
    try {return jObj.getBoolean("brights");} catch (Exception e){return false;}
  }

  private static String loadinfo_parseGWAD(JSONObject jObj){
    //> Handles 'use NO gwad' spec; and "none" is REQUIRED to distinguish such 
    try {if(jObj.getBoolean("gwad")==false){return "none";}} catch (Exception e){;}
    //> Handles explicit gwad specification
    try {if(jObj.getString("gwad")!=null && !jObj.getString("gwad").isEmpty()){return jObj.getString("gwad");}} catch (Exception e){;}
    //> Handles no gwad specification, and in this case `null` implies "use standard" e.g. EisEquip
    return null;
  }

  private static SpecFlag loadinfo_parseFlags(JSONObject jObj){
    if(jObj.getString("flags")!=null && jObj.getString("flags").contains("ALT_LEV_WAD")){
      return SpecFlag.ALT_LEV_WAD;
    }
    return null;
  }
}