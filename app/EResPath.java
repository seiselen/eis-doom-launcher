package app;

/** <b>(Enum of App Resource Paths)</b> Enumerates all resources (sub)paths. */
public enum EResPath {
  /** Directory (in src codebase) with built <code>JAR</code> and app assets. */
  BUILDIR("build"),
  /** Directory (in <code>build/</code>) containing assets. */
  ASSETDIR("assets"),
  /** Filename of <code>JSON</code> containing required path info. */
  SRCPATHS("source_paths.json"),
  /** Filename of app text font (i.e. within {@link #ASSETDIR}). */
  TXTFONT("space-grotesk.vlw"),
  /** Filename of app glyph font (i.e. within {@link #ASSETDIR}). */
  SYMFONT("font_awesome_48.vlw"),
  /** Filename of app logo image (i.e. within {@link #ASSETDIR}). */
  APPLOGO("appname_logo.png"),
  /** Filename of app icon image (i.e. within {@link #ASSETDIR}). */
  APPICON("appIcon.png"),

  /** Filename of (Ultimate) <b>Doom</b> <code>iWAD</code>. */
  FN_DOOM("doom.wad"),
  /** Filename of <b>Doom II</b> <code>iWAD</code>. */
  FN_DOOM2("doom2.wad"),
  /** Filename of <b>Plutonia</b> (Experiment) <code>iWAD</code>. */
  FN_PLUT("plutonia.wad"),
  /** Filename of <b>TNT</b> (Revolution) <code>iWAD</code>. */
  FN_TNT("tnt.wad"),
  /** Filepath of (Ultimate) <b>Doom</b> <code>iWAD</code>. */  
  FP_DOOM,
  /** Filepath of <b>Doom II</b> <code>iWAD</code>. */  
  FP_DOOM2,  
  /** Filepath of <b>Plutonia</b> (Experiment) <code>iWAD</code>. */  
  FP_PLUT,
  /** Filepath of <b>TNT</b> (Revolution) <code>iWAD</code>. */
  FP_TNT,

  /** <code>GZDoom</code> executable (i.e. <code>EXE</code>) filepath. */
  FP_GZDOOM,
  /** Brightmaps <code>WAD/PK3</code> filepath. */
  FP_BRIGHT,
  /** <code>WAD/PK3</code> collection dirpath. */    
  DP_WADS,
  /** <code>iWAD</code>s dirpath. */  
  DP_IWADS, 
  /** Gameplay <code>WAD/PK3</code> filepath. */
  FP_GPLAY,

  STUB; //> exists only for semicolon, lol ;-)

  private String sPath;
  EResPath(){sPath=null;}
  EResPath(String in_sPath){sPath = in_sPath;}
  public String get(){return sPath==null ? this.toString() : sPath;}
}