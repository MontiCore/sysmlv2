package 'Inverter' {
	port def Booleans {
		in attribute val: boolean;
	}

  part def Inverter {
		port input: Booleans;
		port output: ~Booleans;

		assert constraint {
		  input.val == <true, 5> implies output.val == <false>
		}
	}
}
