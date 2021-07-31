action takePicture : TakePicture {
  action trigger accept scene : Scene;

  then action focus : Focus {
    in item scene = trigger::scene;
    out item image;
  }

  then action shoot : Shoot {
    in item image stream from focus::image;
    out item picture;
  }

  then send Show(shoot::picture) to screen;
}
