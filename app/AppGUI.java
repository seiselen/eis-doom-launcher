package app;

import PrEis.gui.AppFont;
import PrEis.gui.ConfirmState;
import PrEis.gui.IActionCallback;
import PrEis.gui.IConfirmAction;
import PrEis.gui.ISelectAction;
import PrEis.gui.IToggleCallback;
import PrEis.gui.LabelType;
import PrEis.gui.PosOri;
import PrEis.gui.UIAppBar;
import PrEis.gui.UIClick;
import PrEis.gui.UIConfirm;
import PrEis.gui.UIContainer;
import PrEis.gui.UIDropdown;
import PrEis.gui.UILabel;
import PrEis.gui.UIManager;
import PrEis.gui.UIToggle;
import PrEis.utils.BBox;
import PrEis.utils.DataStructUtils;
import PrEis.utils.JAResourceUtil;
import PrEis.utils.PrEisRes;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.IntDict;
import processing.data.JSONObject;

class ToggleBrightAction implements IToggleCallback {
  public boolean getState() {return LoadConfig.USE_GZDOOM_STD_BRIGHTS;}
  public void toggleState() {LoadConfig.USE_GZDOOM_STD_BRIGHTS = !LoadConfig.USE_GZDOOM_STD_BRIGHTS;}
}

class ToggleGModAction implements IToggleCallback {
  public boolean getState() {return LoadConfig.DISABLE_GPLAY_OVERRIDE;}
  public void toggleState() {LoadConfig.DISABLE_GPLAY_OVERRIDE = !LoadConfig.DISABLE_GPLAY_OVERRIDE;}
}

public class AppGUI {

  int SELPANE_XOFF    = 16;
  int SELPANE_YOFF    = 80;
  int SELPANE_TALL    = 448;
  int SELPANE_WIDE    = 800;

  int DDOWN_WIDE      = 400;
  int DDOWN_TALL      = 608;
  int DDOWN_YOFF      = 112;
  int DDOWN_XOFF      = AppMain.CANVAS_WIDE-DDOWN_WIDE-16;

  int XBUT_XOFF = AppMain.CANVAS_WIDE-168;
  int XBUT_YOFF = 0;
  int XBUT_WIDE = 160;
  int XBUT_TALL = 40;

  private static final String LCH_BTN_INIT = "LAUNCH CURRENT CONFIG SELECTION";
  private static final String LCH_BTN_WARN = "CLICK AGAIN TO CONFIRM + LAUNCH";
  private static final String LCH_BTN_DONE = "GZDOOM CLI LAUNCHED WITH CONFIG";

  private static final String TOG_BTN_BRIT = "If toggled on: the GZDoom brightmaps PK3 file will be loaded.";
  private static final String TOG_BTN_GMOD = "If toggled on: the specified gameplay mod will NOT be loaded.";
  
  AppUtils appUtil;
  UIManager uim;
  IntDict  glyphDict;

  public AppGUI(AppUtils iAppUtils){
    appUtil = iAppUtils;
    uim = new UIManager(appUtil.app);
    initCustomGlyphs();
    init();
  }


  private void initCustomGlyphs(){
    JSONObject glyphMap = JAResourceUtil.getJSONObjectFromJAR(PrEisRes.SYM_CMAP);
    glyphDict = new IntDict();
    String[] keys = DataStructUtils.keyArrayOfJSONObj(glyphMap);
    for (String k : keys){glyphDict.add(k,glyphMap.getInt(k));}
  }

  private String glyphChar(String n){return ""+(char)glyphDict.get(n);}

  public void init(){

    UIAppBar.create(uim)
    .bindAppLogoΘ(appUtil.app.loadImage(AppMain.fullPathInAssetDir(EResPath.APPLOGO)), new PVector(600, 64))
    .setStyleProp("fill", Integer.class, appUtil.app.color(32))
    ;

    uim.bindUiObject(new UILauncherInfoPane(
      appUtil.app,
      new BBox(SELPANE_XOFF, SELPANE_YOFF, SELPANE_WIDE, SELPANE_TALL),
      appUtil
    ));


    UIContainer.create(
      uim,
      new BBox(SELPANE_XOFF, SELPANE_YOFF+SELPANE_TALL+32, SELPANE_WIDE, 64)   
    )
    .addChildren(

      UILabel.create(
        appUtil.app, new BBox(80, 16, 288, 32),
        "Use STD Brightmaps PK3?", AppFont.TEXT, LabelType.TP, null
      )
      .setTitle(TOG_BTN_BRIT)
      .setStyleProp("txt_anchor", PosOri.class, PosOri.LFT)
      .setStyleProp("txt_offset", PVector.class, new PVector(8,0))
      .setStyleProp("strk_transp", Integer.class, appUtil.app.color(255,0))
      ,

      UILabel.create(
        appUtil.app, new BBox((SELPANE_WIDE/2)+80, 16, 288, 32),
        "Disable Gameplay Mod?", AppFont.TEXT, LabelType.TP, null
      )
      .setTitle(TOG_BTN_GMOD)
      .setStyleProp("txt_anchor", PosOri.class, PosOri.LFT)
      .setStyleProp("txt_offset", PVector.class, new PVector(8,0))
      .setStyleProp("strk_transp", Integer.class, appUtil.app.color(255,0)),



      UIToggle.create(
        appUtil.app,
        new BBox(16, 8, 48, 48),
        glyphChar("toggOn1"),
        AppFont.GLYPH,
        new ToggleBrightAction()
      ).withOnOffLabels(glyphChar("toggOn1"), glyphChar("toggOff1"))
      .setTitle(TOG_BTN_BRIT)
      .setStyleProp("txt_offset", PVector.class, new PVector(1,3))
      .setStyleProp("txt_size", Integer.class, 40),
    

      UIToggle.create(
        appUtil.app,
        new BBox((SELPANE_WIDE/2)+16, 8, 48, 48),
        glyphChar("toggOn1"),
        AppFont.GLYPH,
        new ToggleGModAction()
      ).withOnOffLabels(glyphChar("toggOn1"), glyphChar("toggOff1"))
      .setTitle(TOG_BTN_GMOD)
      .setStyleProp("txt_offset", PVector.class, new PVector(1,3))
      .setStyleProp("txt_size", Integer.class, 40)
      
    )
    .setStyleProp("fill", Integer.class, appUtil.app.color(0,128))
    .setStyleProp("strk_enabled", Integer.class, appUtil.app.color(224,240,255))    
    ;


    UIConfirm cfirm = UIConfirm.create(
      uim,
      new BBox(SELPANE_XOFF, SELPANE_YOFF+SELPANE_TALL+128, SELPANE_WIDE, 64),
      new LaunchButtonAction(appUtil)
    )
    .setButtonLabelsΘ(LCH_BTN_INIT, LCH_BTN_WARN, LCH_BTN_DONE)
    .setDisabledΘ(true)
    .setStyleProp("txt_size", Integer.class, 40)
    .castTo(UIConfirm.class)
    ;



    UIClick.create(
      uim,
      new BBox(appUtil.app.width-480, 12, 288, 40),
      "LOAD PREVIOUS SESSION",
      AppFont.TEXT,
      new LoadPrevCfigAction(appUtil,cfirm)
    )
    .setStyleProp("strk_enabled", Integer.class, appUtil.app.color(255,255,0))    
    .setStyleProp("fill", Integer.class, appUtil.app.color(96,64,0))
    .setStyleProp("fill_hovered", Integer.class, appUtil.app.color (128,96,0))
    .setStyleProp("fill_clicked", Integer.class, appUtil.app.color (160,128,0))
    .setStyleProp("fill_disabled", Integer.class, appUtil.app.color(64,32,0))    
    .setTitle("Loads config used in most recent launch.")
    
    ;


    UIDropdown.create(uim, new BBox(DDOWN_XOFF, DDOWN_YOFF, DDOWN_WIDE, DDOWN_TALL))
    .addOptions(appUtil.getDropdownPayload())
    .bindAction(new WADSelectAction(appUtil,cfirm))
    .setStyleProp("strk_enabled", Integer.class, appUtil.app.color(224,240,255))
    ;

    UILabel.create(
      uim,
      new BBox(DDOWN_XOFF, DDOWN_YOFF-32, DDOWN_WIDE, 32),
      "SELECT WAD FROM DROPDOWN", AppFont.TEXT, LabelType.TP, null
    )
    .setStyleProp("txt_size", Integer.class, 24)
    .setStyleProp("fill_txt", Integer.class, appUtil.app.color(0,64,160))
    .setStyleProp("strk_transp", Integer.class, appUtil.app.color(224,240,255))
    .setStyleProp("fill_transp", Integer.class, appUtil.app.color(208,224,255))
    ;





  }



  public void onMousePressed(){uim.onMousePressed();}
  public void onMouseWheel(int v){uim.onMouseWheel(v);}
  public void update(){
    uim.update();
  }
  public void render(){
    uim.render();
  }
  
}


class LaunchButtonAction implements IConfirmAction {
  ConfirmState curState;
  AppUtils     appUtils;

  public LaunchButtonAction(AppUtils iAppUtils){
    appUtils = iAppUtils;
    curState = ConfirmState.ONINIT;
  }

  public void action() {switch (curState) {
    case ONINIT: curState = ConfirmState.ONWARN; return;
    case ONWARN: curState = ConfirmState.ONDONE; doAction(); return;
    case ONDONE: if(appUtils.getDebugNoLaunch()){cancel(); return;}
    default: return;
  }}

  public void cancel() {curState = ConfirmState.ONINIT;}

  public ConfirmState getState(){return curState;}

  private void doAction(){
    if(appUtils.curCfig==null){cancel(); return;}
    appUtils.launchGZDoomWithCurConfig();
  }
}


class WADSelectAction implements ISelectAction {
  AppUtils  appUtils;
  UIConfirm launchBtn;
  public WADSelectAction(AppUtils in_appUtils, UIConfirm in_launchBtn){
    appUtils  = in_appUtils;
    launchBtn = in_launchBtn;
  }
  public void OnSelection(String wadID) {
    if(launchBtn.isDisabledState()){launchBtn.toggleDisabled();}
    appUtils.onSelectWAD(wadID);}
}


class AppQuitAction implements IConfirmAction {
  private PApplet app;
  private ConfirmState cs;
  public AppQuitAction(PApplet iApp){app = iApp; cs = ConfirmState.ONINIT;}
  public void cancel(){cs = ConfirmState.ONINIT;}
  public ConfirmState getState(){return cs;}
  public void doAction(){app.exit();}
  public void action(){switch (cs){
    case ONINIT: cs = ConfirmState.ONWARN; return;
    case ONWARN: cs = ConfirmState.ONDONE; doAction(); return;
    default: return;
  }}
}


class LoadPrevCfigAction implements IActionCallback {
  AppUtils  appUtils;
  UIConfirm launchBtn;
  public LoadPrevCfigAction(AppUtils in_appUtils, UIConfirm in_launchBtn){
    appUtils  = in_appUtils;
    launchBtn = in_launchBtn;
  }
  public void action() {
    LoadConfig cfig = appUtils.getPrevConfig();
    if(cfig!=null){
      appUtils.setCurConfig(cfig);
      if(launchBtn.isDisabledState()){launchBtn.toggleDisabled();}
    }
  }
}