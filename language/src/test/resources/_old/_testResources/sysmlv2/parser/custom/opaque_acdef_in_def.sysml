action def TakePicture {
  in item scene;
  out item picture;

  action def UpdateSensors (in sensors : Sensor[*]) {
    language "Alf"
      /*
       * for (sensor in sensors) {
       *   if (sensor.ready) {
       *     Update(sensor);
       *   }
       * }
       */
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
