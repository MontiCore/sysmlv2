package 'Inverter' {
	port def Booleans {
		in attribute val: boolean;
	}

  part def Inverter {
		port input: Booleans;
		port output: ~Booleans;

		assert constraint {
		  input.val == <true> implies output.val == <false>
		}
	}
}
