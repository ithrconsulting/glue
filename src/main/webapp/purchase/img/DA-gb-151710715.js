/* Autogenerated at Wed Dec 05 11:07:04 PST 2012 */
;
/* Source: DA.js */
(function(k,o,B){var u=location.protocol,K=navigator.userAgent.toLowerCase(),l=function(a,b){if(a)if(a.length>=0)for(var c=0,e=a.length;c<e;c++)b(c,a[c]);else for(c in a)b(c,a[c])},v=function(a,b){if(a&&b)for(var c in b)a[c]=b[c];return a},L=function(a,b,c){b=b||k;G(b.document)?a():(c&&(a=function(){setTimeout(a,c)}),m(b,"load",a,!0))},G=function(a){var b=a.readyState;return b=="complete"||a.tagName=="script"&&b=="loaded"},s=function(a){return(a=RegExp(a+"[ /](\\d+(\\.\\d+)?)",
"i").exec(K))?+a[1]:0},H=s("msie");s("webkit");var f=k.DA||(k.DA=[]),M=function(a){if(typeof a=="object")return a;var b={};l(a.split(";"),function(a,e){var d=e.split("=");b[d[0]]=d[1]});(k.aanParams=k.aanParams||{})[b.slot]=a;return b},g=function(a,b){return typeof a=="string"?(b||o).getElementById(a):a},n=function(a,b){return(b=g(b||o))?b.getElementsByTagName(a):[]},w=function(a,b,c,e,d){a=(w[a]||(w[a]=o.createElement(a))).cloneNode(!0);v(a,b);p(a,c);e&&(b=a,e=g(e),b=g(b),e&&b&&(typeof d=="number"&&
(d=g(e).childNodes[d]),e.insertBefore(b,d||null)));return a},p=function(a,b){var c=b.opacity;isNaN(c)||v(b,{filter:"alpha(opacity="+c*100+")",mozOpacity:b.opacity});(a=g(a))&&v(a.style,b)},C=function(a){if(a=g(a)){var b=g(a).parentNode;b&&b.removeChild(a)}},E=function(a,b){if(a=g(a))a.innerHTML=b},m=function(a,b,c,e){if(a=g(a)){var d=function(j){var j=j||k.event,N=j.target||j.srcElement;if(e){var i=a,h=d;if(i=g(i))i.removeEventListener?i.removeEventListener(b,h,!1):i.detachEvent?i.detachEvent("on"+
b,h):delete i["on"+b]}return c(j,N,d)};a.addEventListener?a.addEventListener(b,d,!1):a.attachEvent?a.attachEvent("on"+b,d):a["on"+b]=d;return d}},O=n("head")[0],P=f.s=function(a){w("script",{src:a.replace(/^[a-z]+:/,u)},0,O)},Q=(u=="http:"?"//g-ecx.":"//images-na.ssl-")+"images-amazon.com/images/G/02/";k.foresterRegion="eu";var R=function(a,b,c,e){var N;var b="<body>"+b+"</body>",d,j,g=a.parentNode.id,i=o.getElementById(g),h=(i||{}).ad||{},x=h.a||{};if(!h.a){var h=parent.aanParams,t;for(t in h)if("DA"+
t.replace(/([a-z])[a-z]+(-|$)/g,"$1")==g)for(var f=h[t].split(";"),n=0,l=f.length;n<l;n++){var m=f[n].split("=");x[m[0]]=m[1]}}var q=function(){if(i){var a=i.getElementsByTagName("iframe")[0],a=(a.contentWindow||a.contentDocument).aanResponse||{};if(a.adId){var a={s:x.site||"",p:x.pt||"",l:x.slot||"",a:a.adId||0,c:a.creativeId||0,n:a.adNetwork||"DART",m:"load",v:d},b=[],c;for(c in a)b.push('"'+c+'":"'+a[c]+'"');(new Image).src=j+escape("{"+b.join(",")+"}")}else setTimeout(q,1E3)}},r=function(a){if(c&&
parent.uDA&&g)parent[a=="ld"?"uex":"uet"](a,g)};if(c)a.z=function(){r("cf")},a.onload=function(){d=new Date-adStartTime;j="//fls-"+e+".amazon.com/1/display-ads-cx/1/OP/?LMET";q();r("ld");boolDaCriticalFeatureEvent&&(k.DAcf--,!k.DAcf&&k.amznJQ&&amznJQ.declareAvailable("DAcf"))};h=navigator.userAgent.toLowerCase();t=/firefox/.test(h);h=/msie/.test(h);N=(f=a.contentWindow)?f.document:a.contentDocument,a=N;if(h){if(b.indexOf(".close()")!=-1)a.close=function(){}}else t||a.open("text/html","replace"),b+=
"<script>document.close()<\/script>";adStartTime=new Date;a.write(b)},I=function(a,b,c){if(a!=void 0&&a!=null){var e=g("DA"+b+"i"),d=g("DA"+c+"iP");if(e&&d)e.punt=function(){R(e,d.innerHTML.replace(/scrpttag/g,"script"),1);(new Image).src=a}}},S=f.f="/aan/2009-09-09/ad/feedback.gb/default?pt=RemoteContent&slot=main&pt2=gb",J=function(a){var b=function(b){if(!o.all&&!/%/.test(a.width)){var c=a.clientWidth;if(c)a.style.width=c+b+"px"}};b(-1);b(1);try{T(a);var c=g(a).parentNode;c&&(a.contentWindow.d16gCollapse?
p(c,{display:"none"}):c.clientHeight||p(c,{height:"auto",marginBottom:"16px"}))}catch(e){}},T=function(a){var b=g(a).parentNode,c=b.ad||a,e=c.f,d=/nsm/.test(b.id);try{a.contentWindow.suppressAdFeedback&&(e=!1)}catch(j){}var f=n("p",b)[0]||w("p",0,{position:d?"absolute":"relative",top:d?"2px":0,right:d?"305px":0,margin:0,height:"14px"},b);if(e&&!n("a",f)[0]){var i=w("a",0,{position:d?"relative":"absolute",top:d?0:"2px",right:d?0:"4px",display:"inline-block",font:"normal 9px Verdana,Arial",cursor:"pointer"},
f);E(i,(d?"Ad<br>":"Advertisement ")+'<b style="display:inline-block;vertical-align:top;margin:1px 0;width:12px;height:12px;background:url('+Q+'da/js/bubble._V1_.png)"></b>');m(i,"click",function(){c.c=c.c||a.id.replace(/[^0-9]/g,"");var b=c.o;k.DAF=[c.c,c.a];var d=c.f||1;d===1&&(d=S+(b?"-dismissible":"")+(k.jQuery&&jQuery.fn.amazonPopoverTrigger?"":"-external"));P(d)});e=function(a){a=/r/.test(a.type);p(i,{color:a?"#e47911":"#004b91",textDecoration:a?"underline":"none"});p(n("b",i)[0],{backgroundPosition:a?
"0 -12px":"0 0"})};m(i,"mouseover",e);m(i,"mouseout",e);e({})}d=a.contentWindow;e=d.document;d=d.isGoldBox||"showGoldBoxSlug"in d;c.b||l(n("img",e),function(a,b){b&&/sm-head/.test(b.src)&&C(g(b).parentNode)});d&&(p(b,{textAlign:"center"}),p(f,{margin:"0 auto",width:"900px"}))};(function(){l(n("iframe"),function(a,b){if(/^DA/.test(b.id))try{L(function(){J(b)},b.contentWindow)}catch(c){}})})();var D=function(a){var b=a.i,c=a.a=M(a.a),e=c.slot,d=a.c,j=a.u,f=a.w=a.w||300,i=a.h=a.h||250,h=a.d||0,x=a.o,
t=a.b,l=u!="https:"||H!=6,l=a.n&&l&&!G(k),o=a.x?a.x.replace(/^[a-z]+:/,u):"/aan/2009-09-09/static/amazon/iframeproxy-18.html",p=a.p,s=a.k,q="DA"+e.replace(/([a-z])[a-z]+(-|$)/g,"$1"),r=g(q),e=a.v,v=a.l,z=a.j,y=a.y,D=function(b){try{a.r&&uDA&&ue.sc[q].wb==1&&(b=="ld"?uex:uet)(b,q)}catch(c){}};if(r&&!n("iframe",r)[0]&&(r.ad=a,!h||U(a,r,h))){try{if(x&&localStorage[q+"_t"]>(new Date).getTime()){C(r);return}}catch(F){}t?(h=/^https?:/,h=h.test(j)?j.replace(h,u):u+"//ad.uk.doubleclick.net/adi/"+j):(h=o+
"#zgb&cb"+q+"&i"+q+(a.r&&uDA&&ue.sc[q].wb==1?"&r1":"")+(e?"&v1":"")+(z?"&j1":""),k["d16g_dclick_"+q]=j);var A=function(a,c,e,i,h,g){h&&D("af");var f=w("iframe",{src:g?"":a,width:c,height:e,id:i||"",title:h||"",frameBorder:0,marginHeight:0,marginWidth:0,allowTransparency:"true",scrolling:"no"},0,r);I(v,b,d,y);if(h){var j=!1;m(f,"load",function(){j||(j=!0,J(f))})}h&&g&&setTimeout(function(){H?f.src=a:f.contentWindow.location.replace(a);I(v,b,d,y)},5)};g(r).childNodes[0]&&E(r,"");j=/%/.test(f)?"":B.ceil(B.random()*
3);A(h,f+j,i,"DA"+b+"i","Advertisement",l);(p||s)&&setTimeout(function(){var a=(new Date).getTime();p&&A("http://aax-eu.amazon-adsystem.com/s/iu3?d=amazon.co.uk&"+p+"&n="+a,0,0);var b=c.pid;if(s&&b)(new Image).src="//secure-us.imrworldwide.com/cgi-bin/m?ci=amazon-ca&at=view&rt=banner&st=image&ca=amazon&cr="+b+"&pc=1234&r="+a},0)}},U=function(a,b,c){var e=function(a){if(a=g(a)){var b=0,c=0,d=a;do b+=d.offsetLeft,c+=d.offsetTop;while(d=d.offsetParent);a=[b,c,a.clientWidth,a.clientHeight]}else a=[0,
0,0,0];a[0]+=a[2]/2;a[1]+=a[3]/2;return a},d=e(b);if(d.join("")=="0000"){var f=function(){D(a)};k.jQuery&&jQuery.searchAjax?jQuery(o).bind("searchajax",f):(b.T=b.T||9,b.T<1E4&&setTimeout(f,b.T*=2));return!1}var m=!0;l(n("iframe"),function(a,b){if(/^DA/.test(b.id)){var f=e(g(b).parentNode),j=B.abs(d[0]-f[0])-(d[2]+f[2])/2,f=B.abs(d[1]-f[1])-(d[3]+f[3])/2;m=m&&j+f>=c}});m||C(b);return m},F=function(a,b){if(isNaN(b.i)){var c;if(b.e){if(k.d16g)c=b.e}else isNaN(b.y)?c=D:f.v2Loaded&&(c=loadErm);if(c)b.i=
a,c(b)}};(k.d16g||!f.E)&&(f.v2Loaded||!f.v2)&&function(){l(f,F);f.push=function(a){var b=f.length;F(b,f[b]=a)}}();var z=k.jQuery;z&&z(o).bind("spATFEvent",function(){f.splice(0,f.length);z(".ap_popover").remove()});var V=["OBJECT","EMBED"],W=["IFRAME","OBJECT","EMBED"],y=[],A,s=function(a,b){var c=b.tagName;if(c=="IFRAME"||c=="OBJECT"||c=="EMBED")A=a.type=="mouseover"?b:0};m(o,"mouseover",s);m(o,"mouseout",s);m(k,"beforeunload",function(){setTimeout(function(){l(V,function(a,c){var e=n(c);l(e,function(a,
b){y.push(b)})});var a=n("IFRAME");l(a,function(a,c){if(/^DA/.test(c.id))try{var e=c.contentWindow.document;l(W,function(a,b){if(n(b,e).length)throw null;})}catch(d){y.push(c)}});l(y,function(a,c){if(c&&c!==A)if(c.tagName=="IFRAME"){var e=g(c).parentNode;e&&E(e,"")}else C(c)})},0)})})(window,document,Math);