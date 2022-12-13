package ConditionalSuccessionExample1 {
  part def Scene;
  part def Image {
    attribute isWellFocused: ScalarValues::Boolean;
  }
  part def Picture;
    action def Focus { in part scene : Scene; out part image : Image; }
    action def Shoot { in part image: Image; out part picture : Picture; }
    action def TakePicture { in part scene : Scene; out part picture : Picture; }


  action takePicture : TakePicture {
    in item scene;
    out item picture;
    first start;
    then focus;
    action focus : Focus {
      in item scene;
      out item image;
      first startFocus;
      then done;
    }

    first focus
      if focus.image.isWellFocused then shoot;

    flow from focus.image to shoot.image;

    action shoot : Shoot {
      in item;
      out item picture;
      first startShoot;
      then done;
    }
    then done;
  }

}
