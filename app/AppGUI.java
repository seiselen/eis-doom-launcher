package app;

import PrEis.gui.ConfirmState;
import PrEis.gui.IConfirmAction;
import PrEis.gui.ISelectAction;
import PrEis.gui.UIConfirm;
import PrEis.gui.UIDropdown;
import PrEis.gui.UIManager;
import PrEis.utils.BBox;
import processing.core.PVector;

public class AppGUI {

  int SELPANE_XOFF    = 4;
  int SELPANE_YOFF    = 128;
  int SELPANE_TALL    = 360;
  int SELPANE_WIDE    = 768;

  int DDOWN_ITEM_WIDE = 420;
  int DDOWN_ITEM_TALL = 32;

  int DDOWN_WIDE      = DDOWN_ITEM_WIDE+64;
  int DDOWN_TALL      = 576;
  int DDOWN_YOFF      = 32;
  int DDOWN_XOFF      = SELPANE_WIDE+8;

  String LCH_BTN_LBL_INIT = "LAUNCH CURRENT CONFIG SELECTION";
  String LCH_BTN_LBL_WARN = "CLICK AGAIN TO CONFIRM + LAUNCH";
  String LCH_BTN_LBL_DONE = "GZDOOM CLI LAUNCHED WITH CONFIG";

  AppUtils appUtil;
  UIManager uim;

  QADSelectionPane shittyPane;

  public AppGUI(AppUtils iAppUtils){
    appUtil = iAppUtils;
    uim = new UIManager(appUtil.app)
    .injectFonts(appUtil.LABELFONT, appUtil.GLYPHFONT);
    init();
  }

  public void init(){

    shittyPane = new QADSelectionPane(
      appUtil,
      vec(SELPANE_XOFF,SELPANE_YOFF),
      vec(SELPANE_WIDE,SELPANE_TALL)
    );

    
    
    UIDropdown.create(uim, box(vec(DDOWN_XOFF,DDOWN_YOFF), vec(DDOWN_WIDE, DDOWN_TALL)))
    .addOptions(appUtil.getDropdownPayload())
    .bindAction(new WADSelectAction(appUtil));


    UIConfirm.create(uim, box(vec(SELPANE_XOFF,SELPANE_YOFF+SELPANE_TALL+32), vec(SELPANE_WIDE,48)),
      new LaunchButtonAction(appUtil)
    ).setButtonLabels(LCH_BTN_LBL_INIT, LCH_BTN_LBL_WARN,LCH_BTN_LBL_DONE);

  }


  private PVector vec(int inX, int inY){return new PVector(inX, inY);}
  private BBox box(PVector pos, PVector dim){return new BBox(pos, dim);}

  public void onMousePressed(){uim.onMousePressed();}
  public void onMouseWheel(int v){uim.onMouseWheel(v);}
  public void update(){uim.update();}
  public void render(){
    uim.render();
    shittyPane.render();
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
  AppUtils appUtils;
  public WADSelectAction(AppUtils iAppUtils){appUtils = iAppUtils;}
  public void OnSelection(String wadID) {
    System.err.println(wadID);
    appUtils.onSelectWAD(wadID);}
}
