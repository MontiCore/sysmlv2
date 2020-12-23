package 'mySeats' {
	value type Euro;

	block Seat {
		value price: Euro;
	}

	block LuxusSeat :> Seat;
}