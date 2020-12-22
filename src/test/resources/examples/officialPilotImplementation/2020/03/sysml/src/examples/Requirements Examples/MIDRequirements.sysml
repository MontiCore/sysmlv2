package MIDRequirements {
	import ScalarValues::*;
	import ISQ::*;
	
    /**
     * The 'percent' unit is a scaling of a dimensionless real number with a scale factor of 1/100.
     */
    part percent: DimensionOneUnit {
		part redefines unitConversion = ConversionByConvention(referenceUnit => one, conversionFactor => 0.01);
	}
	
	package RequirementParameters {
	   /**
	    * The maximum allowed grizy-band response uniformity.
	    */
		value filtUniformity_grizy : DimensionOneValue = 1.5@[percent];
	 
		/**
		 * The maximum allowed u-band filter response uniformity.
		 */
		value filtUniformity_u : DimensionOneValue = 2.5@[percent];
		
		//...
	}
	
	package RequirementDefinitions {
		import RequirementParameters::*;
		
		requirement def id 'REQ 0238 03' FilterResponseUniformity {
			/* The wavelength of the blue and red 50% response points of the response function at 
			 * any given point in time within the filter clear aperture shall not deviate by more than 
			 * 'filtUniformity_grizy' and 'filtUniformity_u' from that of the area weight filter 
			 * response function
			 */
		}
		
		//...
	}
	
	package 'LSE-30 Observatory System Specification (OSS)' {
		import RequirementDefinitions::*;
	
		//...
		
		package '03 Detailed Specifications'{
			//...
			
			package '03 System Throughput'{
				package '01 Filter Response' {
					requirement id 'OSS REQ 0235 01' FilterResponse {
						//...
						
						// The following requirement usage is owned by the containing 'FilterResponse' requirement.
						requirement id 'OSS REQ 0238 03' OSSFilterResponseUniformity: FilterResponseUniformity {
							
							// The following redefinitions provide specific values for the parameters from the definition.
							value redefines filtUniformity_grizy = 1.5@[percent];
							value redefines filtUniformity_u = 2.5@[percent];
							
						}
						
						//...
					}
				}
			}
		}
	}
	
	
}