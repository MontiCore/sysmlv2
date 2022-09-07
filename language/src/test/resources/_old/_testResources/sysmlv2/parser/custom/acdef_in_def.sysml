action def TakePicture {
  in item scene;
  out item picture;

  action def InnerDefintiion {
    action focus : Focus {
      in item scene = takePicture::scene;
      out item image;
    }
  }

  action focus : Focus {
    in item scene = takePicture::scene;
    out item image;
  }

  succession focus
    if focus::image::isFocused then shoot;

  action shoot : Shoot {
    in item image stream from focus::image;
    out item picture = takePicture::picture;
  }
}
