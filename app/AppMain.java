package app;
import PrEis.utils.FileSysUtils;
import PrEis.utils.JAResourceUtil;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.io.File;

public class AppMain extends PApplet {

  public static int CANVAS_WIDE = 1280;
  public static int CANVAS_TALL = 768;
  public static int CANVAS_WIDH = CANVAS_WIDE/2;
  public static int CANVAS_TALH = CANVAS_TALL/2;

  public static String assetPath;
  public static String rootPath;  
  public AppUtils autils;
  public AppGUI agui;
  public int FILL_BG;

  public static void main(String[] args) {
    PApplet.main("app.AppMain"); 
    System.out.println("\n \n"); //> corrects debug launch blurb lack of newline
  }

  public void settings(){
    this.size(CANVAS_WIDE, CANVAS_TALL); 
  }
  
  public void setup(){
    initAssetPath();
    initRootPath();
    JAResourceUtil.app = this;
    autils = new AppUtils(this);
    agui = new AppGUI(autils);
    FILL_BG = color(0,16,48);

  }

  public void draw(){
    //-[UPDATE CALLS]----------#
    background(FILL_BG);
    agui.update();
    //-[RENDER CALLS]----------#
    agui.render();
  }

  public void mouseWheel(MouseEvent e){
    agui.onMouseWheel(e.getCount());
  }

  public void mousePressed(){
    agui.onMousePressed();
  }

  public void keyPressed(){
    if(key=='q' || key=='Q' || keyCode==PApplet.ESC){exit(); return;}
  }

  public void initAssetPath(){
    File f;
    String ad = EResPath.ASSETDIR.get();
    String bd = EResPath.BUILDIR.get();

    //> Existence of `root/assets` implies standalone mode
    f = new File(FileSysUtils.pathConcat(sketchPath(), ad));
    if (f.exists() && f.isDirectory()){AppMain.assetPath = f.getAbsolutePath();}

    //> Else existence of `root/build/assets` implies development mode
    f = new File(FileSysUtils.pathConcat(sketchPath(), bd, ad));
    if (f.exists() && f.isDirectory()){AppMain.assetPath = f.getAbsolutePath();}
  }

  public void initRootPath(){
    File f = new File(FileSysUtils.pathConcat(sketchPath(), EResPath.BUILDIR.get())); 
    //> Existence of `root/build/` implies development mode
    if (f.exists() && f.isDirectory()){AppMain.rootPath = f.getAbsolutePath();}
    //> Else root is as `sketchPath` specifies
    else{AppMain.rootPath = sketchPath();}
  }

  /** Returns path concat of {@link #assetPath} with input subpath therefrom. */
  public static String fullPathInAssetDir(EResPath sp){
    return FileSysUtils.pathConcat(AppMain.assetPath,sp.get());
  }

  /** Returns path concat of {@link #rootPath} with input subpath therefrom. */
  public static String fullPathInRootDir(EResPath sp){
    return FileSysUtils.pathConcat(AppMain.rootPath,sp.get());
  }


}