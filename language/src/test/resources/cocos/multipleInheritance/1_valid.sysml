package Inheritance {
	import ScalarValues::*;
	part def A{
	attribute f : X;
	}
	part def B{
		attribute f : X;
	}
	part def C :> A,B{
	}

	attribute def X;
}
