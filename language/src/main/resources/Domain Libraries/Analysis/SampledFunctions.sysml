standard library package SampledFunctions {
	doc
	/*
	 * This package provides a library model of discretely sampled mathematical functions.
	 */

	private import Base::Anything;
	private import ScalarValues::Positive;
	private import Collections::KeyValuePair;
	private import Collections::OrderedMap;
	private import SequenceFunctions::size;
	private import ControlFunctions::forAll;
	private import ControlFunctions::collect;
	private import ControlFunctions::select;

	attribute def SampledFunction :> OrderedMap {
		doc
		/*
	     * SampledFunction is a variable-size, ordered collection of 'SamplePair' elements that represents a generic, discretely sampled,
	     * uni-variate or multi-variate mathematical function. The function must be montonic, either strictly increasing or strictly
	     * decreasing.
	     *
	     * It maps discrete domain values to discrete range values.
	     * The domain of the function is represented by the sequence of 'domainValue' of each 'SamplePair' in 'samples', and
	     * the range of the function is represented by the sequence of 'rangeValue' of each 'SamplePair' in 'samples'.
	     */

		attribute samples: SamplePair[0..*] ordered :>> elements;

		assert constraint {
			// Note: Assumes the functions '<' and '>' are defined for the domain type.
			(1..size(samples)-1)->forAll { in i; (preFL samples.domainValue#(i) samples.domainValue#(i+1)) } or  // Strictly increasing
            (1..size(samples)-1)->forAll { in i; (preFG samples.domainValue#(i) samples.domainValue#(i+1)) }     // Strictly decreasing
		}
	}
}
