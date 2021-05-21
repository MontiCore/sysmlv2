part camera : Camera {

  perform action takePhoto[*] ordered
    :> takePicture;

  part f : AutoFocus {
    perform takePhoto::focus;
  }

  part i : Imager {
    perform takePhoto::shoot;
  }
}
