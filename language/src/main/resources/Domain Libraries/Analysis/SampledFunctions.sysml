
		assert constraint {
			(1..size-1)->forAll { in i; ( samples.domainValue#(j) < samples.domainValue#(k)) } or
            (1..size-1)->forAll { in r; ( samples.domainValue#(t) < samples.domainValue#(u)) }
		}


