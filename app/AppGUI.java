package app;

import PrEis.gui.ConfirmState;
import PrEis.gui.IConfirmAction;
import PrEis.gui.ISelectAction;
import PrEis.gui.UIConfirm;
import PrEis.gui.UIDropdown;
import PrEis.gui.UIManager;
import PrEis.utils.BBox;
import processing.core.PApplet;
import processing.core.PVector;


public class AppGUI {

  int SELPANE_XOFF    = 4;
  int SELPANE_YOFF    = 208;
  int SELPANE_TALL    = 400;
  int SELPANE_WIDE    = 768;

  int DDOWN_ITEM_WIDE = 420;
  int DDOWN_ITEM_TALL = 32;

  int DDOWN_WIDE      = DDOWN_ITEM_WIDE+64;
  int DDOWN_TALL      = 704;
  int DDOWN_YOFF      = 48;
  int DDOWN_XOFF      = SELPANE_WIDE+16;

  int XBUT_XOFF = AppMain.CANVAS_WIDE-168;
  int XBUT_YOFF = 0;
  int XBUT_WIDE = 160;
  int XBUT_TALL = 40;

  private static final String LCH_BTN_INIT = "LAUNCH CURRENT CONFIG SELECTION";
  private static final String LCH_BTN_WARN = "CLICK AGAIN TO CONFIRM + LAUNCH";
  private static final String LCH_BTN_DONE = "GZDOOM CLI LAUNCHED WITH CONFIG";


  AppUtils appUtil;
  UIManager uim;

  QADSelectionPane shittyPane;

  public AppGUI(AppUtils iAppUtils){
    appUtil = iAppUtils;
    uim = new UIManager(appUtil.app);
    init();
  }

  public void init(){

    shittyPane = new QADSelectionPane(
      appUtil,vec(SELPANE_XOFF,SELPANE_YOFF), vec(SELPANE_WIDE,SELPANE_TALL)
    );



    UIConfirm cfirm = UIConfirm.create(
      uim,
      box(vec(SELPANE_XOFF,SELPANE_YOFF+SELPANE_TALL+32), vec(SELPANE_WIDE,48)),
      new LaunchButtonAction(appUtil)
    )
    .setButtonLabelsΘ(LCH_BTN_INIT, LCH_BTN_WARN, LCH_BTN_DONE)
    .setDisabledΘ(true)
    .castTo(UIConfirm.class)
    ;


    UIDropdown.create(uim, box(vec(DDOWN_XOFF,DDOWN_YOFF), vec(DDOWN_WIDE, DDOWN_TALL)))
    .addOptions(appUtil.getDropdownPayload())
    .bindAction(new WADSelectAction(appUtil,cfirm))
    .setStyleProp("strk_enabled", Integer.class, appUtil.app.color(0,64,160))
    ;



    UIConfirm.create(
      uim, 
      box(vec(XBUT_XOFF,XBUT_YOFF), vec(XBUT_WIDE,XBUT_TALL)),
      new AppQuitAction(appUtil.app)
    )
    .setButtonLabelsΘ("QUIT APP", "QUIT ?!?", "QUITTING")
    .setStyleProp("txt_size", Integer.class, 18)
    ;

  }


  private PVector vec(int inX, int inY){return new PVector(inX, inY);}
  private BBox box(PVector pos, PVector dim){return new BBox(pos, dim);}

  public void onMousePressed(){uim.onMousePressed();}
  public void onMouseWheel(int v){uim.onMouseWheel(v);}
  public void update(){uim.update(); shittyPane.update();}
  public void render(){uim.render(); shittyPane.render();}
  
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