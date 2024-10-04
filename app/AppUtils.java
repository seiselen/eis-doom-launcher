package app;
import java.util.HashMap;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import PrEis.gui.UIObject;
import PrEis.utils.Cons;
import PrEis.utils.FileSysUtils;
import PrEis.utils.FormatUtils;
import PrEis.utils.JAResourceUtil;
import PrEis.utils.PrEisRes;
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

    APPICON = app.loadImage(AppMain.fullpathOf(EResPath.APPICON));    
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
    try {srcDirsJSON = app.loadJSONObject(AppMain.fullpathOf(EResPath.SRCPATHS));} 
    catch (Exception e){Cons.err(StringUtils.concatAsSSV("File",EResPath.SRCPATHS.get(),"failed to load!")); app.exit(); return;}
    pathdefs.put(EResPath.FP_GZDOOM, srcDirsJSON.getString(EResPath.FP_GZDOOM.get()));
    pathdefs.put(EResPath.DP_WADS,   srcDirsJSON.getString(EResPath.DP_WADS.get()));
    pathdefs.put(EResPath.DP_IWADS,  srcDirsJSON.getString(EResPath.DP_IWADS.get()));
    pathdefs.put(EResPath.FP_GPLAY,  srcDirsJSON.getString(EResPath.FP_GPLAY.get()));
    pathdefs.put(EResPath.FP_BRIGHT, srcDirsJSON.getString(EResPath.FP_BRIGHT.get()));
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
    else{curCfig = newCurConfig;}
  }

  private void loadAndInitOptions(){
    String [] wadNames = getMapsetWadDirNames(); 
    ArrayList <LoadConfig> lcArrList = new ArrayList<LoadConfig>();
    for (String n : wadNames){lcArrList.addAll(Arrays.asList(ConfigBuilder.build(this,n)));}
    for(LoadConfig c : lcArrList){wadCfigs.put(c.value, c);}
  }

  @SuppressWarnings("deprecation") public void launchGZDoomWithCurConfig(){
    if(DEBUG_NO_LAUNCH){Cons.log(curCfig.toLaunchCommand()); return;}
    try{Runtime.getRuntime().exec(curCfig.toLaunchCommand()); app.exit(); return;}
    catch (IOException e){Cons.err(e.getMessage());}
  }

}