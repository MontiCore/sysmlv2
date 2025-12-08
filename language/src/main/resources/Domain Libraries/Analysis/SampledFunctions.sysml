
		assert constraint {
			(1..size(samples)-1)->forAll { in i; ( samples.domainValue#(i) < samples.domainValue#(i+1)) } or
            (1..size(samples)-1)->forAll { in i; ( samples.domainValue#(i) > samples.domainValue#(i+1)) }
		}


