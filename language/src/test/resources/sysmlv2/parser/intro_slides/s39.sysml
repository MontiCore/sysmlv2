action takePicture : TakePicture {
  in item scene;
  out item picture;

  action focus : Focus {
    in item scene = takePicture::scene;
    out item image;
  }

  succession focus then shoot;

  action shoot : Shoot {
    in item image stream from focus::image;
    out item picture = takePicture::picture;
  }
}
