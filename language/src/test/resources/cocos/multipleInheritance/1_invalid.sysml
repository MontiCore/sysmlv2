package Inheritance {
	import ScalarValues::*;
	part def A{
	attribute f : X;
	}
	part def B{
		attribute f : Y;
	}
	part def C :> A,B{
		attribute f : X :>> f;
	}

	attribute def X;

	attribute def Y;

}
