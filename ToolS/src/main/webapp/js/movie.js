var i = 0;
function right(max) {
	var left = Number($('.recommend-list').position().left);
    left = left - 928;
    i++;
    var $thisDot = $('.dot.active');
    $thisDot.removeClass('active');
    if (i == max)
  	  $('.dot:first').addClass('active');
    else
  	  $thisDot.next('.dot').addClass('active');
    $('.recommend-list').animate({left: left + 'px'}, 900, function() {
      if (i == max) {
        //left = -928;
        $('.recommend-list').css({left: '-919px'});
        //alert($('.recommend-list').position().left)
        i = 0;
      }
    })
}


function left(max) {
	var left = Number($('.recommend-list').position().left);
    left = left + 928;
    i--;
    var $thisDot = $('.dot.active');
    $thisDot.removeClass('active');
    if (i == -1)
        $('.dot:last').addClass('active');
    else
        $thisDot.prev('.dot').addClass('active');
    $('.recommend-list').animate({left: left + 'px'}, 900, function() {
      if (i == -1) {
        $('.recommend-list').css({left: '-3703px'});
        i = max - 1;
        //alert(i);
      }
    })
}

$(function() {
	    //$('img').lazyload();
        if (!(document.cookie || navigator.cookieEnabled)) {
        	$('#download-list').hide();
        }
        //var i = 0;
        //alert($('.recommend-list').html())
        var w = $(window).width();
    	
        var max = $('.recommend-list li').length;
        var fli = $('.recommend-list li').first().clone();
        var lli = $('.recommend-list li').last().clone();
        $('.recommend-list').prepend(lli);
        $('.recommend-list').append(fli);
        $('.recommend-list').css({left: '-919px'});
        $('.icon-angle-right').click(function() {
        	if($(".recommend-list:animated").length == 0) {
        		right(max);
        	}
        })
        $('.icon-angle-left').click(function() {
        	if($(".recommend-list:animated").length == 0) {
        		left(max);
        	}
        });
        
        $('#rightMovieTabs a').click(function (e) {
    	  e.preventDefault();
    	  $(this).tab('show');
    	});
        
        $('.other-year-list a').click(function() {
        	$('span.btn-year').html($(this).html());
        });
        
        $('.slide-control .dot').click(function() {
        	if($(".recommend-list:animated").length == 0) {
	            var active_index = $('.dot.active').index() - 1;
	        	var click_index = $('.slide-control .dot').index(this);
	        	if (active_index < click_index) {
		        	var left = Number($('.recommend-list').position().left);
		        	i = click_index;
		            left = left - (i - active_index) * 928;
		            var $thisDot = $('.dot.active');
		            $thisDot.removeClass('active');
	            	$('.slide-control .dot').eq(click_index).addClass('active');
		            $('.recommend-list').animate({left: left + 'px'}, 900, function() {
		              if (i == max) {
		                //left = -928;
		                $('.recommend-list').css({left: '-919px'});
		                //alert($('.recommend-list').position().left)
		                i = 0;
		              }
		            });
	        	} else if (active_index > click_index) {
	        		var left = Number($('.recommend-list').position().left);
		        	i = click_index;
		            left = left + (active_index - i) * 928;
		            var $thisDot = $('.dot.active');
		            $thisDot.removeClass('active');
	            	$('.slide-control .dot').eq(click_index).addClass('active');
	        	    $('.recommend-list').animate({left: left + 'px'}, 900, function() {
	        	      if (i == -1) {
	        	        $('.recommend-list').css({left: '-3703px'});
	        	        i = max - 1;
	        	        //alert(i);
	        	      }
	        	    })
	        	}
        	}
        });
        var stime = window.setInterval(function() {
        	if($(".recommend-list:animated").length == 0) {
        		$('.icon-angle-right').click();
        	}
        }, 5000);
        
        $('.jumbotron').mouseover(function(e) {
    		window.clearInterval(stime);
        });
        $('.jumbotron').mouseout(function(e) {
        	window.clearInterval(stime);
        	if (typeof stime != 'undefined') {
	        	stime = window.setInterval(function() {
	        		if($(".recommend-list:animated").length == 0) {
	        			$('.icon-angle-right').click();
	        		}
	            }, 5000);
        	}
        });
        
        /**
         * 切换大图显示
         */
        $('.icon-th-large').click(function() {
        	ajaxChangeDisplay('grid');
        });
        
        /**
         * 切换列表显示
         */
        $('.icon-list').click(function() {
        	ajaxChangeDisplay('list');
//        	var fids = '';
//        	$('.movie-large').each(function() {
//        	    fids = fids + $(this).attr('fid') + ',';
//        	});
//        	
//        	$('.movie-large').hide();
//        	$('.movie-list').show();
//        	
//        	if (fids != '') {
//        		fids = fids.substring(0, fids.lastIndexOf(','));
//        		ajaxGetMovieAbout(fids);
//        	}
        });
        
        /*注册*/
        $('#imageCode').blur(function() {
        	var imageCode = $(this).val();
        	var $imageCode = $(this);
        	if (imageCode == '') {
        		$imageCode.parent().find('.err_msg').html('请填写验证码');
        		return false;
        	}
        	$.ajax({
        		url: '/imageCode/register/check',
        		method: 'post',
        		data: {'validImageCode': imageCode},
        		success: function(data) {
        			$imageCode.parent().find('.err_msg').html(data);
        		}
        	})
        });
        $('#clickImageCode').click(function() {
        	var src = $(this).attr('src');
        	$(this).attr('src', src + '?v=' + Math.random());
        })
        $('#saveBtn').click(function() {
        	var $registerForm = $('#registerForm');
        	var $email = $registerForm.find('input[name=email]');
        	var $password = $registerForm.find('input[name=password]');
        	var $user_name = $registerForm.find('input[name=user_name]');
        	$('.err_msg').html('');
        	if ($email.val() == '') {
        		$email.parent().find('.err_msg').html('邮箱不能为空');
        		return false;
        	} else {
        		if (!isEmail($email.val())) {
        			$email.parent().find('.err_msg').html('请填写正确的邮箱');
        			return false;
        		}
        	}
        	if ($password.val() == '') {
        		$password.parent().find('.err_msg').html('密码不能为空');
        		return false;
        	} else {
        		if ($password.val().length  < 6) {
        			$password.parent().find('.err_msg').html('密码最少6位');
        			return false;
        		}
        	}
        	if ($('#imageCode').val() == '') {
        		$('#imageCode').parent().find('.err_msg').html('请填写验证码');
        		return false;
        	}
        	if ($user_name.val() == '') {
        		$user_name.parent().find('.err_msg').html('用户名不能为空');
        		return false;
        	}
        	$registerForm.submit();
        });
        /*注册*/
        
        /*登录*/
        $('#loginBtn').click(function() {
        	$('#loginForm').submit();
        });
        $('.login-form').mouseover(function() {
        	//$(this).find('.user-home').show();
        }).mouseleave(function() {
        	var $this = $(this);
    		//$this.find('.user-home').hide();
        });
        $('.login-form span.user-name').click(function() {
        	//$(this).parent().find('.user-home').show();
        });
        /*登录*/
        
        /*用户相关*/
        $('.share-find').click(function() {
    		var $shareMUrl = $(this).parent().find('.share-m-url');
    		var shareUrl = $shareMUrl.val();
    		var doubanUrlReg = /^(http|https):\/\/movie\.douban\.com\/subject\/(\d+)/i
    		var doubanIdReg = /^(\d+)$/i
    		if (doubanUrlReg.test(shareUrl) || doubanIdReg.test(shareUrl)) {
    			$('#searching-modal').modal({
    				keyboard: false,
    				backdrop: 'static'
    			});
    			$('#findForm').submit();
    		} else {
    			alert("请填写正确的豆瓣电影链接或ID");
    			return false;
    		}
    	});
        /*用户相关*/
        
        $('.early-toggle').click(function() {
        	$(this).hide();
        	$(this).next('.input-year-span').show();
        	return false;
        });
        
        $('.input-year-btn').click(function() {
        	var year = $(this).prev().val();
        	if (year != '' && (isNaN(year) || year.length < 4)) { //不是数字
        		return false;
        	}
        	var dataHref = $(this).parent().prev('.early-toggle').attr('data-href');
        	if (year != '') dataHref = dataHref + '?y=' + year;
        	window.location.href = dataHref;
        });
});

function isEmail(mail) {
    var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    if (filter.test(mail)) {
        return true; 
    } else {
        return false;
    } 
}


$('.d-tools .order li').click(function() {
	var $dToolsLi = $('.d-tools .order li');
	$dToolsLi.removeClass('prev-active')
	$dToolsLi.removeClass('active');
	$(this).addClass('active');
	if ($(this).prev().length > 0)
		$(this).prev().addClass('prev-active');
});

$('[hide]').hide();


$(window).on("resize", function() {
	windowLoad();
});

function windowLoad() {
	var content_w = $('.content').width();
	
	var w = $(window).width();
	var mainW = $('.main').width();
	
	var searchFormWidth = $('.search-form').width();
    if (mainW <= 792) {
    	$('.search-form .form-group').css('width', '100%');
    } else {
    	$('.search-form .form-group').css('width', '');
    }
    $('.search-form').css('visibility', 'visible');
    
	if (mainW < 950) {
		var liWidth = mainW * (154 / 950);
		if (liWidth > 103)
			$('.recommend-list-div li').css('width', mainW * (154 / 950) - 5);
		else $('.recommend-list-div li').css('width', 103);
		
	}
	if ($('.jumbotron').is(":hidden")) {
		$('.jumbotron').show();
	}
	$('.recommend-list-div .thumbnail').each(function() {
		var height = $(this).height();
		//$(this).parent().css('height', height);
	})
	var mmd2 = $('.recommend-list-div .thumbnail').height();
	//$('.recommend-list-div .m-md-2').css('height', mmd2);
	
	var h = $(window).height() - 230;
	$('#batch-download-model textarea').css('height', h);
    $('#batch-download-model .modal-dialog').css('margin-top', ($(window).height() - h - 120) / 2);
    
    if ($('.tv-download-title').length > 0) {
    	$('.tv-download-ul').each(function() {
    		changeDownloadList($(this));
    	})
    }
    if ($('.add-share-text').length > 0) {
    	$('.add-share-text').css('width', '100%');
    }
}

function changeDownloadList($tvDownloadUl) {
	var c = true;
	//var $tvDownloadUl = $(this);
	$tvDownloadUl.find('.download-title').css('width', 'auto');
	$tvDownloadUl.find('.download-btn').each(function() {
		//if ($(this).parent().parent().hasClass('gt-1')) {
			var liwidth = $(this).parent().width();
			var tvtitlewidth = $(this).parent().find('.download-title').width() + 5;
			var tvdownloadwidth = $(this).width() + 5;
//			if ($tvDownloadUl.hasClass('gt-1')) {
//				alert(liwidth)
//			}
			if (liwidth <= (tvtitlewidth + tvdownloadwidth)) {
//				if ($tvDownloadUl.hasClass('gt-2')) {
//					alert(c)
//				}
				if (c) {
					$tvDownloadUl.find('.download-btn').css('float', 'left');
					$tvDownloadUl.find('.download-title').css('width', '100%');
					c = false;
					return false;
				}
			} else {
				$(this).css('float', 'right');
			}
		//}
	});
}
function ajaxChangeDisplay(show_type) {
	$.ajax({
		url: '/ajaxChangeDisplay',
		type: 'post',
		data: {'show_type': show_type},
		success: function(result) {
			window.location.reload();
		}
	});
}

function ajaxGetMovieAbout(fids) {
	$.ajax({
		url: '/ajaxGetMovieAbout',
		type: 'post',
		data: {'info_ids': fids},
		success: function(result) {
			$('.movie-large').each(function() {
				var fid = $(this).attr('fid');
				var movie_list = $('.movie-list[fid=' + fid + ']')
				if (result[fid].other_name != 'null' && result[fid].other_name != '') {
					movie_list.find('.other-name').html(result[fid].other_name);
				}
				if (result[fid].director != '') {
					var director = result[fid].director;
					movie_list.find('.ot1').html('导演：' + director);
				}
				if (result[fid].actors != '') {
					var actors = result[fid].actors;
					movie_list.find('.ot2').html('主演：' + actors);
				}
//				alert(result[fid].about);
			});
		}
	});
}

function sdt(code) {
    code = unescape(code);
    var c=String.fromCharCode(code.charCodeAt(0)-code.length);
    for(var i=1;i<code.length;i++) {
        c+=String.fromCharCode(code.charCodeAt(i)-c.charCodeAt(i-1));
    }
    return c;
}

$('.more-a').click(function() {
	$('.more-d').hide();
	$(this).hide();
	$('.more-txt').show();
});
windowLoad();
//(function(){
//      var bp = document.createElement('script');
//      var curProtocol = window.location.protocol.split(':')[0];
//      if (curProtocol === 'https') {
//          bp.src = 'https://zz.bdstatic.com/linksubmit/push.js';        
//      }
//      else {
//          bp.src = 'http://push.zhanzhang.baidu.com/push.js';
//      }
//      var s = document.getElementsByTagName("script")[0];
//      s.parentNode.insertBefore(bp, s);
//})();

//电影详情页ajax调用下载链接加密前代码
//if ($('#download-list').length > 0) {
//    $.ajax({
//        url: '/tv/${str_m_id}/downloadList',
//        data: {
//            'decriptParam': '${decriptParam}'
//        },
//        type: 'get',
//        success: function(data) {
//            $('#tv-download').html(data);
//            $('#ajax-load').hide();
//            $('#tv-download').show();
//        }
//    })
//}
//if ($('#download-list').length > 0) {
//    $.ajax({
//    	url: '/movie/${str_m_id}/downloadList',
//    	data: {'decriptParam': '${decriptParam}'},
//    	type: 'get',
//    	success: function(data) {
//    		$('#movie-download').html(data);
//    		$('#ajax-load').hide();
//    		$('#movie-download').show();
//    	}
//    })
//}