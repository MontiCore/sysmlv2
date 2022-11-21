package ConditionalSuccessionExample1 {
	part def Scene;
	part def Image {
		attribute isWellFocused: ScalarValues::Boolean;
	}
	part def Picture;

	action takePicture : TakePicture {


		action focus : Focus {

		}

		first focus
			if focus.image.isWellFocused then shoot;

		flow focus.image to shoot.image;

		action shoot : Shoot {
		}
	}

}
