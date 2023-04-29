/**
 * 
 */

	function confirmEmail() {
		var emailRegExp = /^[A-Za-z0-9+]*$/;

		
		var inputEmail = document.getElementById("inputEmail");
		
		var emailConfirmPwAlertBox = document.getElementById("emailConfirmPwAlertBox");
		
		if(!emailRegExp.test(inputEmail.value)) {
			emailConfirmPwAlertBox.innerText = "이메일 주소를 형식에 맞게 입력해주세요";
			emailConfirmPwAlertBox.style.color = "red";
			confirm_email = false;
		} else {
			emailConfirmPwAlertBox.innerText = "";
			confirm_email = true;
		}
		
	}
	
	
	function confirmEmailDomain() {
		var domainRegExp = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
		var inputEmailDomain = document.getElementById("inputEmailDomain");
		
		var emailConfirmPwAlertBox = document.getElementById("emailConfirmPwAlertBox");
		
		if(!domainRegExp.test(inputEmailDomain.value)) {
			emailConfirmPwAlertBox.innerText = "이메일 도메인을 형식에 맞게 입력해주세요";
			emailConfirmPwAlertBox.style.color = "red";
			confirm_emailDomain = false;
		} else {
			emailConfirmPwAlertBox.innerText = "";
			confirm_emailDomain = true;
		}
	}	    
    
    function setEmailDomain(domain){
        $("#inputEmailDomain").val(domain);
        $("#emailConfirmPwAlertBox").text("");
        confirm_emailDomain = true;        
    }
    
	function selectJob(btn) {
		if(btn.classList.contains("on")) {
			btn.classList.remove("on");
		} else {
			btn.classList.add("on");			
		}
		
		job_no_list = [];
		for(var e of document.querySelectorAll(".job-btn-r-gray.on")) {
			job_no_list.push(e.value);
		}
	}
	
	function selectLocal(btn) {
		if(btn.classList.contains("on")) {
			btn.classList.remove("on");
		} else {
			btn.classList.add("on");			
		}
		
		local_no_list = [];
		for(var e of document.querySelectorAll(".local-btn-r-pink.on")) {
			local_no_list.push(e.value);
		}		
	}    
    
	function setThumbnail(event) { 
		var reader = new FileReader(); 
		
		reader.onload = function(event) { 
			
			var img = document.querySelector("#profile_thumbnail img"); 
			img.setAttribute("src", event.target.result);  			
		}; 
		
		reader.readAsDataURL(event.target.files[0]); 
	}    
    
	function createBirthSelect(customer_birth) {
		var now = new Date();
		var year = now.getFullYear();
		var mon = (now.getMonth() + 1) > 9 ? ''+(now.getMonth() + 1) : '0'+(now.getMonth() + 1);
		var day = (now.getDate()) > 9 ? ''+(now.getDate()) : '0'+(now.getDate()); 
		
		//년도 selectbox만들기 
		for(var i = 1900 ; i <= year ; i++) { 
			$('#inputBirth_yy').append('<option value="' + i + '">' + i + '년</option>');
		} 
		
		// 월별 selectbox 만들기 
		for(var i=1; i <= 12; i++) {
			var mm = i > 9 ? i : "0"+i ;
			$('#inputBirth_mm').append('<option value="' + mm + '">' + mm + '월</option>');
		}
		
		// 일별 selectbox 만들기
		for(var i=1; i <= 31; i++) {
			var dd = i > 9 ? i : "0"+i ;
			$('#inputBirth_dd').append('<option value="' + dd + '">' + dd+ '일</option>');
		}
		
		if(customer_birth != null) {
			setBirth(customer_birth);
		} else {
			$("#inputBirth_yy > option[value="+year+"]").attr("selected", "true");
			$("#inputBirth_mm > option[value="+mon+"]").attr("selected", "true");
			$("#inputBirth_dd > option[value="+day+"]").attr("selected", "true");			
		}
		
	}
	
	function setBirth(birth) {
		birth = new Date(birth);
		var year = birth.getFullYear();
		var mon = (birth.getMonth() + 1) > 9 ? ''+(birth.getMonth() + 1) : '0'+(birth.getMonth() + 1);
		var day = (birth.getDate()) > 9 ? ''+(birth.getDate()) : '0'+(birth.getDate()); 

		$("#inputBirth_yy > option[value="+year+"]").attr("selected", "true");
		$("#inputBirth_mm > option[value="+mon+"]").attr("selected", "true");
		$("#inputBirth_dd > option[value="+day+"]").attr("selected", "true");
	}
	
	function onlyKorean(event) {
		
		if((event.keyCode < 12592) || (event.keyCode > 12687)){
			event.returnValue = false;
		}
				
	}
	
	function slice(obj) {
		if(obj.value.length > obj.maxLength){
			obj.value = obj.value.slice(0, obj.maxLength);
        }			
	}
	
	function onlyNumber(obj) {
		
		/*if ((event.keyCode < 48) || (event.keyCode > 57))
            event.returnValue = false;*/
            
            obj.value = obj.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');            

	}
	
	
	function confirmPw(obj) {
		//정규표현식...
		var pwRegExp = /(?=.*\d)(?=.*[a-zA-ZS]).{8,}/;
		
		
		var confirmPwAlertBox = document.getElementById("afterReg");
		
		if(!pwRegExp.test(obj.value)){
			confirm_pw = false;
			confirmPwAlertBox.innerText = "❗ 문자/숫자 1개 포함 8자리이상";
			confirmPwAlertBox.classList.add("text-red-soft");
			confirmPwAlertBox.classList.remove("text-green-soft");
		} else {
			confirm_pw = true;
			confirmPwAlertBox.innerText = "비밀번호 사용 가능";
			confirmPwAlertBox.classList.remove("text-red-soft");
			confirmPwAlertBox.classList.add("text-green-soft");
		}
		
	}	
	
	function reConfirmPw(obj) {
		
		var reConfirmPwAlertBox = document.getElementById("afterConfirm")
		
		if( $("#newPw").val() != obj.value ){
			confirm_pw2 = false;
			reConfirmPwAlertBox.innerText = "변경 비밀번호 불일치";
			reConfirmPwAlertBox.classList.add("text-red-soft");
			reConfirmPwAlertBox.classList.remove("text-green-soft");
		} else {
			confirm_pw2 = true;
			reConfirmPwAlertBox.innerText = "변경 비밀번호 일치";
			reConfirmPwAlertBox.classList.remove("text-red-soft");
			reConfirmPwAlertBox.classList.add("text-green-soft");
		}
		
	}	
	
	
	//사업자번호
	function inputBizNumber(obj) {

		obj.value = obj.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
		
		var number = obj.value.replace(/[^0-9]/g, "");
		var biz = "";
		
		
		
		if(number.length < 4) {
		    return number;
		} else if(number.length < 5) {
			biz += number.substr(0, 3);
			biz += "-";
			biz += number.substr(3);
		} else if(number.length < 10) {
			biz += number.substr(0, 3);
			biz += "-";
			biz += number.substr(3, 2);
			biz += "-";
			biz += number.substr(5);
		} else {
			biz += number.substr(0, 3);
			biz += "-";
			biz += number.substr(3, 2);
			biz += "-";
			biz += number.substr(5);
		}
		obj.value = biz;
	}		
	
	function findAddr(){
		new daum.Postcode({
	        oncomplete: function(data) {
				
				// 도로명 주소 변수
				var roadAddr = data.roadAddress;
				// 지번 주소 변수
				var jibunAddr = data.jibunAddress;
				
	            if(roadAddr !== ''){
	                document.getElementById("inputAddress").value = roadAddr;
	            } 
	            else if(jibunAddr !== ''){
	                document.getElementById("inputAddress").value = jibunAddr;
	            }
	        }
	    }).open();
	}	
 	
	
	