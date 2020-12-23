package automobile {
	import bolts::*;
	import mySeats::*;
	import wrongImport::*;

	block 'vehicle' {
		part seats : Seat[*];
		part luxusSeat : LuxusSeat :> Seats;
	}
}