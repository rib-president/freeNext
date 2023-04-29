/**
 * 
 */ 
	
    function scrollAble(){
        $('html, body').removeClass('scrolloff');
    }

    function scrollDisable(){
        $('html, body').addClass('scrolloff');
    }
    
	function pollUnRead() {
				
		if(sessionUser == null) return;

		var xhr = new XMLHttpRequest();
		
		xhr.onreadystatechange = function() {
			if(xhr.readyState == 4 && xhr.status == 200) {
				var data = JSON.parse(xhr.responseText);
				
				if(data.unReadCount > 0) {
					createUnReadBadge(data.unReadCount);	
				} else {
					if(document.querySelector(".alarm-circle") != null) {
						document.querySelector(".alarm-circle").remove();
					}
					if(document.querySelector(".alarm-count") != null) {
						document.querySelector(".alarm-count").remove();	
					}
					
				}							
			}
		}
		
		xhr.open("get", "/normal/getUnReadAlarmCount?member_no=" + sessionUser.member_no, true);
		xhr.send();
	}
	
	function createUnReadBadge(count) {
		var btnMenu = document.querySelector(".Btn_Menu");
				
		if(document.querySelector(".alarm-circle") == null) {
			var redLight = document.createElement("span");
			redLight.setAttribute("class", "alarm-circle position-absolute top-1 translate-middle p-2 bg-danger border border-light rounded-circle");
			redLight.style.right = "-0.75rem";
			btnMenu.appendChild(redLight);			
		}
		
		var myBox = document.querySelector("#myBox");
		
		if(document.querySelector(".alarm-count") != null) {
			document.querySelector(".alarm-count").remove();
		}
		
		var alarmCount = document.createElement("span");
		alarmCount.setAttribute("class", "alarm-count ms-2 badge rounded-pill bg-danger text-fs-14");
		if(count < 100) {
			alarmCount.innerText = count;	
		} else {
			alarmCount.innerText = "99+";
		}
		
		myBox.appendChild(alarmCount);
		 
	}
	
	window.addEventListener("DOMContentLoaded" , function(){
		if(sessionUser != null) {
			pollUnRead();
			
			setInterval(pollUnRead, 600000);
		}		        
	});
