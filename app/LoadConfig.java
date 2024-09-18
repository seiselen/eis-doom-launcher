package app;
import PrEis.utils.FileSysUtils;
import PrEis.utils.FormatUtils;
import PrEis.utils.StringUtils;

/** Encompasses all files that can be specified for launch via GZDoom CLI. */
public class LoadConfig {

  AppUtils appUtils;

  /** Config Name/ID */
  String value;
  /** Config Label */
  String label;
  /** GZDoom app */
  String fpath_gzd;
  /** Internal WAD */
  String fpath_iwad;
  /** DeHackEd file */
  String fpath_deh;
  /** Mapset WAD|PK3 */
  String fpath_wad;
  /** Gameplay WAD|PK3 */
  String fpath_gwad;
  /** Brightmaps PK3 */
  String fpath_brit;

  public LoadConfig(AppUtils iAppUtils){
    appUtils   = iAppUtils;
    value  = "";
    label = "";
    initDefaultVals();
  }

  public void initDefaultVals(){
    fpath_gzd  = appUtils.getFilepath(EResPath.FP_GZDOOM);
    fpath_iwad = appUtils.getFilepath(EResPath.FP_DOOM2);
    fpath_gwad = appUtils.getFilepath(EResPath.FP_GPLAY);
    fpath_brit = appUtils.getFilepath(EResPath.FP_BRIGHT);    
  }

  /*----------------------------------------------------------------------------
  |> CHAIN SETTERS
  +---------------------------------------------------------------------------*/

  private void setValΘ(String n){
    if(n!=null){value = FileSysUtils.fnameFromFpath(n,false);}
  }

  private void setLblΘ(String l){
    if(l!=null){label = StringUtils.valToSSVCapdWords(l);}
  }  

  public LoadConfig setVal(String cname){
    setValΘ(cname); return this;
  }

  public LoadConfig setLbl(String clabl){
    setLblΘ(clabl); return this;
  }

  public LoadConfig setFilepath_GZD(String fp){
    if(fp!=null){fpath_gzd=fp;}
    return this;
  }
  
  public LoadConfig setFilepath_WAD(String fp){
    if(fp!=null){fpath_wad=fp;}
    return this;
  }
  
  public LoadConfig setFilepath_DEH(String fp){
    if(fp!=null){fpath_deh=fp;}
    return this;
  }
  
  public LoadConfig setFilepath_IWAD(String fp){
    if(fp!=null){fpath_iwad=fp;}
    return this;
  }
  
  public LoadConfig setFilepath_BRIT(boolean v){
    if(!v){fpath_brit=null;}
    return this;}
  
  public LoadConfig setFilepath_GWAD(String fp){
    if(fp!=null){fpath_gwad=(fp.equals("none")) ? null : fp;}
    return this;
  }

  /*----------------------------------------------------------------------------
  |> UTIL FUNCTIONS
  +---------------------------------------------------------------------------*/

  private String fileSubCommand(){
    String ret = "";
    //> no mapset WAD nor gameplay WAD ⮕ no filespec
    if(fpath_iwad==null&&fpath_gwad==null){return ret;}
    //> one or both of them exist ⮕ prefix filespec
    ret += " -file";
    if(fpath_wad!=null){ret+=" "+fpath_wad; if(fpath_deh!=null){ret+=" "+fpath_deh;}}
    if(fpath_brit!=null){ret+=" "+fpath_brit;}    
    if(fpath_gwad!=null){ret+=" "+fpath_gwad;}
    return ret;
  }
  
  public String toLaunchCommand(){
    return fpath_gzd+" -iwad "+fpath_iwad+fileSubCommand();
  }

  public static String configToLaunchArgs(String gzdoomFPATH, String iwadFPATH, String wadFPATH, String eisequipFPATH){
    return gzdoomFPATH+" -iwad "+iwadFPATH+" -file "+wadFPATH+" "+eisequipFPATH;
  }
  

  /*----------------------------------------------------------------------------
  |> TOSTRING/TOCONSOLE FUNCTIONS
  +---------------------------------------------------------------------------*/

  public String[] getDropdownValAndLbl(){
    return new String[]{value, label};
  }

  public String toString(){
    return 
      propToStr("Config Name/ID ---------->",  value) +
      propToStr("Config Label ------------>", label) +    
      propToStr("GZDoom App Filepath ----->",  fpath_gzd) +
      propToStr("iWAD Filepath ----------->", fpath_iwad) +
      propToStr("DeHackEd Filepath ------->",  fpath_deh) +
      propToStr("Subject WAD Filepath----->",  fpath_wad) +
      propToStr("Brightmaps PK3 Filepath ->", fpath_brit) +    
      propToStr("Gameplay WAD Filepath --->", fpath_gwad) +
      propToStr("Resultant CLI Command --->", toLaunchCommand());
  }

  private String propToStr(String pfix, String prop){
    return pfix+" "+StringUtils.wrapWith('[', FormatUtils.strValElseNone(prop))+" \n";
  }

  public void toConsole(){
    System.out.println(this.toString());
  }
}