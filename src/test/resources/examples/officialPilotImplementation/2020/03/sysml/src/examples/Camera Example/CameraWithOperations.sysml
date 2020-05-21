package CameraWithOperations {
	
	activity PowerOn;
	activity TakePicture(in location, out picture);
	activity PowerOff;
	
	activity StorePicture(in picture);
	
	block Camera {	
		port cameraPort {
			ref out powerOn: PowerOn;
			ref out takePicture: TakePicture;
			ref out powerOff: PowerOff;
		}
		
		port diskPort {
			ref in storePicture: StorePicture;
		}
		
		private action powerOnMethod: PowerOn = cameraPort::powerOn { /* ... */ }
		private action takePictureMethod: TakePicture = cameraPort::takePicture { 
			perform shoot(out picture);
			then perform diskPort::storePicture(in picture flow from shoot::picture);
			then done;
		}
		private action powerOffMethod: PowerOff = cameraPort::powerOff { /* ... */}
		
		private action shoot(out picture);
	}
	
	block DiskDevice {
		port storagePort {
			ref out storePicture: StorePicture;
		}
		
		private action storePictureMethod: StorePicture = storagePort::storePicture { /* ... */}
	}
	
	part context {
		part camera: Camera {
			port redefines diskPort {
				ref in redefines storePicture;
			}
		}
		
		part diskDevice: DiskDevice {
			port redefines storagePort {
				ref out redefines storePicture;
			}
		}
		
		bind camera::diskPort::storePicture = diskDevice::storagePort::storePicture;
	}
	
}