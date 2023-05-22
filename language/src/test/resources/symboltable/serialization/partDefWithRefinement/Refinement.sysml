/* (c) https://github.com/MontiCore/monticore */
package Refinement {
  part def UnfairMedium { }
  part def FairMedium refines UnfairMedium { }
  part def PerfectMedium refines FairMedium { }
}
