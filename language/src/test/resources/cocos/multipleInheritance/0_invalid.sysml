package Inheritance {
	import ScalarValues::*;
	part def A{
	attribute f : X;
	}
	part def B{
		attribute f : Y;
	}
	part def C :> A,B{

	}

	attribute def X;

	attribute def Y;

	attribute def Z :> X,Y;
}
