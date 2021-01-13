package Vehicles{
  package a{
    block Car;
  }
  package b {
  	block Car;
  }
  package c {
    import Vehicles::a::*;
    import Vehicles::b::*;
    part c: Car;
  }
}