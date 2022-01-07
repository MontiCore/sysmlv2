action takePicture : TakePicture {
  first start;

  then merge continue;

  then action trigger { out item scene; }

  then action focus : Focus {
    in item scene stream from trigger::scene;
    out item image;
  }

  then action shoot : Shoot {
    in image stream from focus::image;
    out picture;
  }

  then action display {
    in picture stream from shoot::picture;
  }
  then continue;
}
