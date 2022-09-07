action def Focus(in scene : Scene, out image : Image);
action def Shoot(in image : Image, out picture : Picture);

action def TakePicture {
 in item scene : Scene;
 out item picture : Picture;

 action focus : Focus {
   in item scene = TakePicture::scene;
   out item image;
 }

 action shoot : Shoot {
   in item image flow from focus::image;
   out item picture = TakePicture::picture;
 }
}