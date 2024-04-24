jQuery(document).ready(function(){
	
	/*토스트팝업*/
	function toastOn(){
		$('.toast-popup').addClass('on').delay(3000).queue(function(){
		    $(this).removeClass("on").dequeue();
		});
	};
	
	$(".toast-popup").click(function(){
		$('html').animate( { scrollTop : 0 }, 400 );
	});
	/*loading*/
	function loadingShow(){
		$('.loading-wrap').addClass('on')
	};
	function loadinghide(){
		$('.loading-wrap').removeClass('on')
	};

	//a링크 스크롤이동 막기
	$('a[href="#"]').click(function(event) {
		event.preventDefault();      
	});

	//뒤로가기
	$(".btn-undo").click(function(){
		history.go(-1)();
	});

	//gnb tab 동작
	$(".tab-menu").click(function(){
		var tabIndex = $(this).index();
		$(".tab-menu").removeClass('active');
    	$(this).addClass('active');
    	$(".tab-con").removeClass('active');
    	$('html').scrollTop(0);
    	$(".tab-con").eq(tabIndex).addClass('active');
  	});

	//nav 동작
	$('.btn-menu').click(function(){
		$('.nav-wrap').addClass('on');
		scrollDisable();
	})
	$('.nav-close').click(function(){
		$('.nav-wrap').removeClass('on');
		scrollAble();
	})

	/*popup*/
	function popupOn(targetPopup){
		$('.popup-wrap').removeClass('on');
		$(targetPopup).addClass('on');
	}
	function popupOff(targetPopup){
		$(targetPopup).removeClass('on');
	}

	$('.popup-trigger').click(function(){
		scrollDisable();
		var targetPopup = $(this).attr('popup-class');
		popupOn(targetPopup);
	});

	$('.popup-close').click(function(){
		scrollAble();
		var targetPopup = $(this).parents('.popup-wrap.on');
		popupOff(targetPopup);
	});

	/*logout UI*/
	$('.logout-btn').click(function(){
		$('.nav-wrap').removeClass('on');
	});

	/*body 스크롤 막기*/
	function scrollDisable(){
    $('body').addClass('scrollDisable').on('scroll touchmove mousewheel', function(e){
        e.preventDefault();
	    });
	}
	function scrollAble(){
	    $('body').removeClass('scrollDisable').off('scroll touchmove mousewheel');
	}

	/*볼륨 UI*/
	$('.volume-t').html( $('.volume').val() );
	$('.volume').on('input', function() {
    	$('.volume-t').html( $(this).val() );
    });

});

var menuplusApp = {
		toastOn : function() {
			$('.toast-popup').addClass('on').delay(3000).queue(function(){
			    $(this).removeClass("on").dequeue();
			});
		},
		loadingShow : function() {
			$('.loading-wrap').addClass('on')
		},
		loadinghide : function() {
			$('.loading-wrap').removeClass('on')
		}
}

	