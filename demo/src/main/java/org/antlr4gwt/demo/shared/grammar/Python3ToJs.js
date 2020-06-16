(function(){
var _base={
'<':'<',
'>':'>',
'==':'==',
'>=':'>=',
'<=':'<=',
'<>':'!=',
'!=':'!=',
'in':'in',
'notin':'return !( $1 in $2 );',
'is':'===',
'isnot':'!==',
'|':'|',
'^':'^' ,
'&':'&' ,
'<<':'<<',
'>>':'>>',
'+':'+',
'-':'-',
'*':'*',
'@':'',
'/':'/',
'%':'%',
'//':'return Math.floor($1/$2);',
'and':'&&',
'or':'||',
'&&':'&&',
'||':'||'
}

for (op in _base){
    let tf=_base[op];
    let func=null;
    let error=new Function(["b"],'throw (typeof this) + " doesn\'t support operator \''+op+'\' ";');
    if(! tf){
    	func=error;
    }
    else if(tf.indexOf('$')>=0){
         tf=tf.replace(/\$1/g,'this').replace(/\$2/g,'b');
    	func=new Function(["b"],tf);;
    }else{
    	var code="return this "+_base[op]+" b; "; 
    	func=new Function(["b"],code);
    }

	Object.prototype[op]=error;
	Number.prototype[op]=func;
	String.prototype[op]=func;
}

String.prototype._substr=function(a,b){ return this.substr(a,b?b-a:b);   }

})();

Object.prototype.__size = function() {
    var size = 0, key;
    for (key in this) {
        if (this.hasOwnProperty(key)) size++;
    }
    return size;
};
const None=null;
const False=false;
const True=true;
const len = function (a){
    if(!a)return 0;
    if((typeof a) == "number" || (typeof a) == "string") return (""+a).length;
	if(a instanceof Object && 'length' in a)return a['length'];
	if(a instanceof Object && 'size' in a)return a['size']();
	
    return a.__size();
}


const __and = function (){
     let a=false;
     for(let i=0;i<arguments.length;i++){
     	  a=arguments[i];
     	  if(typeof (a) == "function"){
     	  	a=a();
     	  }else if(a===null || a===0 || a===false || len(a)==0){
     	  	 return a; 
     	  }
     }
    return a;
}

const __or = function (){
     let a=true;
     for(let i=0;i<arguments.length;i++){
     	  a=arguments[i];
     	  if(typeof (a) == "function"){
     	  	a=a();
     	  }
     	  if(a===null || a===0 || a===false || len(a)==0){
     	  	  continue;
     	  }
     	  return a;
     	  
     }
    return a;
}


const __test = function (a){

   if(a==null || a===0 || a===false || len(a)==0){
     	 return false;
    }
    return true;
}
