
		assert constraint {
			(1..size(samples)-1)->forAll { in i; ( samples.domainValue#(j) < samples.domainValue#(k+1)) } or
            (1..size(samples)-1)->forAll { in r; ( samples.domainValue#(t) > samples.domainValue#(u+1)) }
		}


