package Vehicles{
	package a{
	  import Vehicles::b::*;
	}
	package b{
    import Vehicles::a::*;
  }
}