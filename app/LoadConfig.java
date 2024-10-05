package app;
import PrEis.utils.FileSysUtils;
import PrEis.utils.StringUtils;

/** Encompasses all files that can be specified for launch via GZDoom CLI. */
public class LoadConfig {

  /** <b>Not Applicable</b> i.e. null */
  public static final String NA = "-(N/A)-";

  /** 
   * Bound to <code>UIToggle</code> s.t. if set <code>true</code>: gameplay WAD
   * will be ignored (i.e. if/as specified by current config selection).
   */
  public static boolean DISABLE_GPLAY_OVERRIDE = false;
  /** 
   * Bound to <code>UIToggle</code> s.t. if set <code>true</code>: 'standard'
   * GZDoom brightmaps <code>PK3</code> will be loaded.
   * @implNote <b>NOTE:</b> The current behavior is that this <b>OVERRIDES</b>
   * any existing brightmap spec! Ergo if a config has one, and it aint working:
   * this is likely why (quick refactor if needed to fix, but #yolo and #KISS).
   */
  public static boolean USE_GZDOOM_STD_BRIGHTS = false;

  AppUtils appUtil;

  /** Config Name/ID */
  String cf_val;
  /** Config Label */
  String cf_lbl;
  /** GZDoom app */
  String fp_gzd;
  /** Internal WAD */
  String fp_iwad;
  /** DeHackEd file */
  String fp_deh;
  /** BoomExDeh file */
  String fp_bex;
  /** Mapset WAD|PK3 */
  String fp_wad;
  /** Gameplay WAD|PK3 */
  String fp_gwad;
  /** Brightmaps PK3 */
  String fp_brit;

  public LoadConfig(AppUtils iAppUtils){
    appUtil = iAppUtils;
    cf_val  = "";
    cf_lbl  = "";
    fp_gzd  = appUtil.getFilepath(EResPath.FP_GZDOOM);
    fp_iwad = appUtil.getFilepath(EResPath.FP_DOOM2);
    fp_gwad = appUtil.getFilepath(EResPath.FP_GPLAY);
    fp_brit = appUtil.getFilepath(EResPath.FP_BRIGHT);    
  }

  /*----------------------------------------------------------------------------
  |> SETTERS
  +---------------------------------------------------------------------------*/

  public LoadConfig setValΘ(String cname){setVal(cname); return this;}
  private void setVal(String iName){
    if(iName!=null){cf_val = FileSysUtils.fnameFromFpath(iName,false);}
  }

  public LoadConfig setLblΘ(String clabl){setLbl(clabl); return this;}
  private void setLbl(String iLabl){
    if(iLabl!=null){cf_lbl = StringUtils.valToSSVCapdWords(iLabl);}
  }

  public LoadConfig setFPΘ(ConfigProp prop, String path){setFP(prop, path); return this;}
  public void setFP(ConfigProp prop, String path){switch (prop){
    case FP_GZD  : if(path!=null){fp_gzd=path;}  return;
    case FP_IWAD : if(path!=null){fp_iwad=path;} return;
    case FP_DEH  : if(path!=null){fp_deh=path;}  return;
    case FP_WAD  : if(path!=null){fp_wad=path;}  return;
    case FP_BRIT : if(path==null){fp_brit=null;} return;
    case FP_GWAD : if(path!=null){fp_gwad=(path.equals("none")) ? null : path;}; return;
    default      : return;
  }}


  /*----------------------------------------------------------------------------
  |> GETTERS
  +---------------------------------------------------------------------------*/

  private boolean hasDehPath(){return fp_deh!=null || fp_bex!=null;}
  private String getDehPath(){return (fp_deh==null) ? fp_bex : fp_deh;}

  private boolean hasBritPath(){return USE_GZDOOM_STD_BRIGHTS || fp_brit!=null;}
  private String  getBritPath(){return (USE_GZDOOM_STD_BRIGHTS) ? appUtil.getFilepath(EResPath.FP_BRIGHT) : fp_brit;}

  public String toLaunchCommand(){
    String ret = fp_gzd + " -iwad "+fp_iwad;
    if(fp_iwad==null&&fp_gwad==null){return ret;}
    //> no mapset WAD nor gameplay WAD ⮕ no filespec
    if(fp_iwad==null&&fp_gwad==null){return ret;}
    //> one or both of them exist ⮕ prefix filespec
    ret += " -file";
    if(fp_wad!=null){ret+=" "+fp_wad;}
    if(hasDehPath()){ret+=" "+getDehPath();}
    if(hasBritPath()){ret+=" "+getBritPath();}   
    if(fp_gwad!=null && !DISABLE_GPLAY_OVERRIDE){ret+=" "+fp_gwad;}
    return ret;
  }

  public String[] getDropdownValAndLbl(){
    return new String[]{cf_val, cf_lbl};
  }

  /** Realized for config info pane getters -VS- expense of Java Reflection. */
  public String getProp(ConfigProp prop){switch (prop) {
    case CF_VAL  : return StringUtils.strValElse(cf_val,NA);
    case CF_LBL  : return StringUtils.strValElse(cf_lbl,NA);
    case FP_GZD  : return StringUtils.strValElse(fp_gzd,NA);
    case FP_IWAD : return StringUtils.strValElse(fp_iwad,NA);
    case FP_DEH  : return StringUtils.strValElse(getDehPath(),NA);
    case FP_WAD  : return StringUtils.strValElse(fp_wad,NA);
    case FP_BRIT : return StringUtils.strValElse(fp_brit,NA);
    case FP_GWAD : return StringUtils.strValElse(fp_gwad,NA);
    case CLI_CMD : return StringUtils.strValElse(toLaunchCommand(), NA);
    default      : return null;
  }}

  private String propToStr(String pfix, String prop){
    return pfix+" "+StringUtils.wrapWith('[', StringUtils.strValElse(prop,NA))+" \n";
  }

  /*----------------------------------------------------------------------------
  |> TOSTRING/TOCONSOLE FUNCTIONS
  +---------------------------------------------------------------------------*/

  public String[][] toKVStrArr(){
    return new String[][]{
      new String[]{"CF_VAL",  getProp(ConfigProp.CF_VAL) },
      new String[]{"CF_LBL",  getProp(ConfigProp.CF_LBL) },
      new String[]{"FP_GZD",  getProp(ConfigProp.FP_GZD) },
      new String[]{"FP_IWAD", getProp(ConfigProp.FP_IWAD)},
      new String[]{"FP_DEH",  getProp(ConfigProp.FP_DEH) },
      new String[]{"FP_WAD",  getProp(ConfigProp.FP_WAD) },
      new String[]{"FP_BRIT", getProp(ConfigProp.FP_BRIT)},
      new String[]{"FP_GWAD", getProp(ConfigProp.FP_GWAD)},
      new String[]{"CLI_CMD", getProp(ConfigProp.CLI_CMD)}
    };
  }

  public String toString(){return 
    propToStr("Config Name/ID ---------->", cf_val) +
    propToStr("Config Label ------------>", cf_lbl) +    
    propToStr("GZDoom App Filepath ----->", fp_gzd) +
    propToStr("iWAD Filepath ----------->", fp_iwad) +
    propToStr("DeHackEd Filepath ------->", fp_deh) +      
    propToStr("BoomExDeh Filepath ------>", fp_bex) +
    propToStr("Subject WAD Filepath----->", fp_wad) +
    propToStr("Brightmaps PK3 Filepath ->", fp_brit) +    
    propToStr("Gameplay WAD Filepath --->", fp_gwad) +
    propToStr("Resultant CLI Command --->", toLaunchCommand());
  }

  public void toConsole(){
    System.out.println(this.toString());
  }
}