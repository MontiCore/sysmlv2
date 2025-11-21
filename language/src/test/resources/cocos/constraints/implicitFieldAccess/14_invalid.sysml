package 'Inverter' {
	port def Booleans {
		in attribute val: boolean;
	}

  part def Inverter {
		port input: Booleans;
		port output: ~Booleans;

		assert constraint {
		  input == <true, 5> implies output == <false>
		}
	}
}
