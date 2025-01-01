package app;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import PrEis.gui.UIObject;
import PrEis.utils.Cons;
import PrEis.utils.DataStructUtils;
import PrEis.utils.FileSysUtils;
import PrEis.utils.FormatUtils;
import PrEis.utils.JAResourceUtil;
import PrEis.utils.PrEisRes;
import PrEis.utils.QueryUtils;
import PrEis.utils.StringUtils;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONObject;

public class AppUtils {

  /** 
   * Disables the final launch step of invoking the <code>GZDoom</code> CLI with
   * the config command; instead only printing it to console. Make sure this is
   * set to <code>false</code> before building a release <i>(duh!)</i>
   */
  private boolean DEBUG_NO_LAUNCH = false;

  /** Used by launch Confirm Button to cancel/reset on debug no launch event. */
  public boolean getDebugNoLaunch(){return DEBUG_NO_LAUNCH;}


  public HashMap<EResPath,String> pathdefs;

  public PFont TXTFONT;
  public PFont SYMFONT;
  public PImage APPLOGO;
  public PImage APPICON;

  HashMap<String,LoadConfig> wadCfigs;
  
  
  LoadConfig curCfig;

  

  /** Public for max exposability to consumers of this 'singleton' instance. */
  public PApplet app;

  public AppUtils(PApplet iApp){
    app = iApp;
    pathdefs = new HashMap<EResPath,String>();
    wadCfigs = new HashMap<String,LoadConfig>();
    initPaths();
    loadAndInitOptions();
    loadAssets();
    app.getSurface().setIcon(APPICON);
    app.getSurface().setTitle("EisDoomLauncher"); 
  }

  
  private void loadAssets(){
    UIObject.textFont = JAResourceUtil.getFontFromJAR(PrEisRes.TXT_FONT);
    UIObject.symbFont = JAResourceUtil.getFontFromJAR(PrEisRes.SYM_FONT);
    UIObject.monoFont = JAResourceUtil.getFontFromJAR(PrEisRes.MON_FONT);

    APPICON = app.loadImage(AppMain.fullPathInAssetDir(EResPath.APPICON));    
  }

  /*============================================================================
  |>>> Getters
  +===========================================================================*/

  public LoadConfig getCurConfig(){
    return curCfig;
  }

  public boolean hasCurConfig(){
    return curCfig!=null;
  }
  
  public String getCurConfigProp(ConfigProp prop){
    if(hasCurConfig()){return curCfig.getProp(prop);} return LoadConfig.NA;
  }

  /*============================================================================
  |>>> Setters
  +===========================================================================*/

  public void setCurConfig(LoadConfig inConfig){
    if(inConfig!=null){curCfig=inConfig;}
  }




  /*============================================================================
  |>>> Global Path Utils
  +===========================================================================*/

  /** <b>Get Fullpath Via Internal Hashmap</b>. */
  public String getFilepath(EResPath pkey){
    return pathdefs.get(pkey);
  }

  /** <b>Get Filepath Via Internal Hashmap</b>. */
  public String filepathOf(EResPath dp, EResPath fn){
    return FileSysUtils.pathConcat(getFilepath(dp),fn.get());
  }

  /** 
   * Initializes filepaths and dirpaths from <code>source_paths.json</code> file
   * as well as those composed therefrom.
   */
  private void initPaths(){
    JSONObject srcDirsJSON = null;
    try {srcDirsJSON = app.loadJSONObject(AppMain.fullPathInAssetDir(EResPath.SRCPATHS));} 
    catch (Exception e){Cons.err(StringUtils.concatAsSSV("File",EResPath.SRCPATHS.get(),"failed to load!")); app.exit(); return;}
    pathdefs.put(EResPath.FP_GZDOOM, srcDirsJSON.getString(EResPath.FP_GZDOOM.get()));
    pathdefs.put(EResPath.DP_WADS,   srcDirsJSON.getString(EResPath.DP_WADS.get()));
    pathdefs.put(EResPath.DP_IWADS,  srcDirsJSON.getString(EResPath.DP_IWADS.get()));
    pathdefs.put(EResPath.FP_GPLAY,  srcDirsJSON.getString(EResPath.FP_GPLAY.get()));
    pathdefs.put(EResPath.FP_BRIGHT, srcDirsJSON.getString(EResPath.FP_BRIGHT.get()));
    pathdefs.put(EResPath.FP_LIGHTS, srcDirsJSON.getString(EResPath.FP_LIGHTS.get()));    
    pathdefs.put(EResPath.FP_DOOM,   filepathOf(EResPath.DP_IWADS, EResPath.FN_DOOM));
    pathdefs.put(EResPath.FP_DOOM2,  filepathOf(EResPath.DP_IWADS, EResPath.FN_DOOM2));
    pathdefs.put(EResPath.FP_PLUT,   filepathOf(EResPath.DP_IWADS, EResPath.FN_PLUT));
    pathdefs.put(EResPath.FP_TNT,    filepathOf(EResPath.DP_IWADS, EResPath.FN_TNT));
  }

  String getFilenameFromDir(String dir){
    ArrayList<String> fnames = new ArrayList<String>();
    try {Files.find(Paths.get(dir), 2, (p,a)->(p.getFileName().toString().endsWith("wad") || p.getFileName().toString().endsWith("pk3"))).forEach(path -> fnames.add(path.toString()));}
    catch(IOException ie) {ie.printStackTrace();}
    return fnames.size()>0 ? fnames.get(0).replace("\\","/") : null;
  }
  
  String[] getWADFilenamesFromDir(String dir){
    ArrayList<String> fnames = new ArrayList<String>();
    try {Files.find(Paths.get(dir), 2, (p,a)->(p.getFileName().toString().endsWith("wad") || p.getFileName().toString().endsWith("pk3"))).forEach(path -> fnames.add(path.toString().replace("\\","/")));}
    catch(IOException ie) {ie.printStackTrace();}
    return FormatUtils.arrFromList(String.class, fnames);
  }
  
  String[] getMapsetWadDirNames(){
    return new File(getFilepath(EResPath.DP_WADS)).list(new FilenameFilter(){@Override public boolean accept(File c, String n){return new File(c, n).isDirectory() && n.charAt(0)!='_';}});
  }
  
  String[] getMapsetWadDirPaths(){
    String[] dirNames = getMapsetWadDirNames();
    String[] dirPaths = new String[dirNames.length];
    for (int i = 0; i < dirNames.length; i++) {dirPaths[i] = FileSysUtils.pathConcat(getFilepath(EResPath.DP_WADS),dirNames[i]);}
    return dirPaths;
  }


  /*============================================================================
  |>>> APP SESSION LOG UTILS
  +===========================================================================*/

  private JSONObject loadAppSessionLog(){
    if(new File(AppMain.fullPathInRootDir(EResPath.APP_LOG)).exists()){
      return app.loadJSONObject(AppMain.fullPathInRootDir(EResPath.APP_LOG));
    }
    else {
      System.out.println("Cannot find app log file in app root dir");
      return null;
    }
  }

  public LoadConfig getPrevConfig(){
    JSONObject appLog = loadAppSessionLog();
    if(appLog==null){return null;}
    if(appLog.keys().size()<1){return null;}
    String[] keys = DataStructUtils.keyArrayOfJSONObj(appLog);
    java.util.Arrays.sort(keys, Collections.reverseOrder());
    return ConfigBuilder.parseAppLogConfig(this, appLog.getJSONObject(keys[0]));
  }


  private void setAppSessionLog(){
    JSONObject appLog = loadAppSessionLog();
    if(appLog==null){appLog = new JSONObject();}
    
    JSONObject sessObj = new JSONObject();
    String[][] sessInfo = curCfig.toKVStrArr();
    sessObj.setString("date",QueryUtils.dateTimeToString());
    for(String[] datum : sessInfo){sessObj.setString(datum[0], datum[1]);}
    appLog.setJSONObject(QueryUtils.epochSecondToString(), sessObj);
    app.saveJSONObject(appLog, AppMain.fullPathInRootDir(EResPath.APP_LOG));
  }



  /*============================================================================
  |>>> WAD Options/Configs Utils
  +===========================================================================*/

  public String[][] getDropdownPayload(){
    String[][] ret = new String[wadCfigs.size()][2];
    int idx = 0;
    for(LoadConfig c : wadCfigs.values()){ret[idx++] = c.getDropdownValAndLbl();}
    java.util.Arrays.sort(ret, new java.util.Comparator<String[]>() {public int compare(String[] a, String[] b){return a[1].compareTo(b[1]);}});
    return ret;
  }

  public void onSelectWAD(String wadID){
    LoadConfig newCurConfig = wadCfigs.get(wadID);
    if(newCurConfig==null){Cons.warn("Cannot find `LoadConfig` of value ["+wadID+"]");}
    else{setCurConfig(newCurConfig);}
  }

  private void loadAndInitOptions(){
    String [] wadNames = getMapsetWadDirNames(); 
    ArrayList <LoadConfig> lcArrList = new ArrayList<LoadConfig>();
    LoadConfig [] buff;
    for (String n : wadNames){
      buff = ConfigBuilder.build(this,n);
      //> universal null or empty array return handling
      if(buff==null||buff.length==0){continue;}
      lcArrList.addAll(Arrays.asList(buff));
    }
    for(LoadConfig c : lcArrList){wadCfigs.put(c.cf_nam, c);}
  }

  @SuppressWarnings("deprecation") public void launchGZDoomWithCurConfig(){
    String CLICMD = curCfig.toLaunchCommand();
    setAppSessionLog();
    if(DEBUG_NO_LAUNCH){System.out.println(CLICMD); return;}
    else{try{Runtime.getRuntime().exec(CLICMD);} catch (IOException e){e.printStackTrace();}}
    app.exit(); return;
  }
}