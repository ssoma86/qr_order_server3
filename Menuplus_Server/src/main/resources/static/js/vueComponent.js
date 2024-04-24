/*<![CDATA[*/
// 공통 코드
Vue.component('combo', {
	props: ['model', 'label', 'data'],
	template: 	'<el-select v-model="model" @change="handleChange" style="width: 100%;">' +
				'	<el-option key="" :label="label" value="" v-if="undefined == label ? false : true"></el-option>' +
				'	<el-option v-for="item in data" :key="item.value" :label="item.label" :value="item.value"></el-option>' +
				'</el-select>',
	methods: {
		handleChange: function (val) {
			this.$emit('change', val);
		}
	}
});

Vue.component('mcombo', {
	props: ['model', 'label', 'data'],
	template: 	'<el-select v-model="model" multiple @change="handleChange" style="width: 100%;">' +
				'	<el-option key="" :label="label" value="" v-if="undefined == label ? false : true"></el-option>' +
				'	<el-option v-for="item in data" :key="item.value" :label="item.label" :value="item.value"></el-option>' +
				'</el-select>',
	methods: {
		handleChange: function (val) {
			this.$emit('change', val);
		}
	}
});

Vue.component('code-combo', {
	props: ['model', 'label', 'data', 'disabled'],
	template: 	'<el-select v-model="modelVal" @change="handleChange" style="width: 100%;" :disabled="disabled">' +
				'	<el-option key=0 :label="label" :value=0 v-if="undefined == label ? false : true"></el-option>' +
				'	<el-option v-for="item in data" :key="item.cd" :label="item.nmLan" :value="item.cd"></el-option>' +
				'</el-select>',
	computed: {
		modelVal: function () {
			if (null != this.model) {
				if (typeof(this.model) == "object" && !Array.isArray(this.model)) {
					return this.model.cd;
				} else {
					return this.model;
				}
			} else {
				return '';
			}
		}
	},
	methods: {
		handleChange: function (val) {
			for (var i = 0, len = this.data.length; i < len; i++) {
				if (this.data[i].cd == val) {
					this.$emit('change', this.data[i]);
					return;
				}
			}
			
			this.$emit('change', { cd: val });
		}
	}
});

Vue.component('mcode-combo', {
	props: ['model', 'label', 'data', 'disabled'],
	template: 	'<el-select v-model="modelVal" multiple @change="handleChange" :disabled="undefined == disabled ? false : disabled" style="width: 100%;">' +
				'	<el-option key=0 :label="label" :value=0 v-if="undefined == label ? false : true"></el-option>' +
				'	<el-option v-for="item in data" :key="item.cd" :label="item.nmLan" :value="item.cd"></el-option>' +
				'</el-select>',
	computed: {
		modelVal: function () {
			var tmpVal = [];
			
			if (null != this.model) {
				for (var i = 0, len = this.model.length; i < len; i++) {
					tmpVal.push(this.model[i].cd);
				}
			}
			return tmpVal;
		}
	},
	methods: {
		handleChange: function (val) {
			var returnVal = [];
			
			for (var i = 0, len = this.data.length; i < len; i++) {
				for (var j = 0, jlen = val.length; j < jlen; j++) {
					if (this.data[i].cd == val[j]) {
						returnVal.push(this.data[i]);
					}
				}
			}
			
			this.$emit('change', returnVal);
		}
	}
});

Vue.component('lan-combo', {
	props: ['model', 'label', 'data'],
	template: 	'<el-select v-model="model" @change="handleChange" style="width: 100%;">' +
				'	<el-option key="" :label="label" value="" v-if="undefined == label ? false : true"></el-option>' +
				'	<el-option v-for="item in data" :key="item.id" :label="item.nm" :value="item.id"></el-option>' +
				'</el-select>',
	methods: {
		handleChange: function (val) {
			this.$emit('change', val);
		}
	}
});

Vue.component('lans-combo', {
	props: ['model', 'label', 'data', 'disable_id'],
	template: 	'<el-select v-model="modelVal" multiple @change="handleChange" style="width: 100%;">' +
				'	<el-option key="" :label="label" value="" v-if="undefined == label ? false : true"></el-option>' +
				'	<el-option v-for="item in data" :key="item.id" :label="item.nm" :value="item.id" :disabled="item.id == disable_id"></el-option>' +
				'</el-select>',
	computed: {
		modelVal: function () {
			var tmpVal = [];
			for (var i = 0, len = this.model.length; i < len; i++) {
				tmpVal.push(this.model[i].id);
			}
			
			return tmpVal;
		}
	},
	methods: {
		handleChange: function (val) {
			var returnVal = [];
			
			for (var i = 0, len = this.data.length; i < len; i++) {
				for (var j = 0, jlen = val.length; j < jlen; j++) {
					if (this.data[i].id == val[j]) {
						returnVal.push(this.data[i]);
					}
				}
			}
			
			this.$emit('change', returnVal);
		}
	}
});

Vue.component('category-combo', {
	props: ['model', 'data'],
	template: 	'<el-select v-model="modelVal" multiple @change="handleChange" style="width: 100%;">' +
				'	<el-option v-for="item in data" :key="item.categoryCd" :label="item.categoryNm" :value="item.categoryCd"></el-option>' +
				'</el-select>',
	computed: {
		modelVal: function () {
			var tmpVal = [];
			
			if (null != this.model) {
				for (var i = 0, len = this.model.length; i < len; i++) {
					console.log("=============this.model["+ i + "] : " + this.model[i].categoryCd);
					tmpVal.push(this.model[i].categoryCd);
				}
			}
			return tmpVal;
		}
	},
	methods: {
		handleChange: function (val) {
			var returnVal = [];
			
			console.log("=============value:" + value);
			
			for (var i = 0, len = this.data.length; i < len; i++) {
				for (var j = 0, jlen = val.length; j < jlen; j++) {
					if (this.data[i].categoryCd == val[j]) {
						returnVal.push(this.data[i]);
					}
				}
			}
			
			console.log("=============returnVal:" + returnVal);
			
			this.$emit('change', returnVal);
		}
	}
});



Vue.component('paymethod-combo', {
	props: ['model', 'data'],
	template: 	'<el-select v-model="modelVal" multiple @change="handleChange" style="width: 100%;">' +
				'	<el-option v-for="item in data" :key="item.val" :label="item.nm" :value="item.val"></el-option>' +
				'</el-select>',
	computed: {
		modelVal: function () {
			var tmpVal = [];
			
			if (null != this.model) {
				for (var i = 0, len = this.model.length; i < len; i++) {
					console.log("=============this.model["+ i + "] : " + this.model[i]);					
					tmpVal.push(this.model[i]);
				}
			}
			
			console.log("=============tmpVal:" + tmpVal);
			
			return tmpVal;
		}
	},
	methods: {
		handleChange: function (value) {
			var returnVal = [];
			
			console.log("=============value:" + value);
			
			for (var i = 0, len = this.data.length; i < len; i++) {
				for (var j = 0, jlen = value.length; j < jlen; j++) {
					if (this.data[i].val == value[j]) {
						returnVal.push(this.data[i].val);
					}
				}
			}
			
			console.log("=============returnVal:" + returnVal);
			
			this.$emit('change', returnVal);
		}
	}
});


Vue.component('opt-tp-combo', {
	props: ['model', 'label', 'data'],
	template: 	'<el-select v-model="model" @change="handleChange" style="width: 100%;">' +
				'	<el-option key="" :label="label" value="" v-if="undefined == label ? false : true"></el-option>' +
				'	<el-option v-for="item in data" :key="item.smenuOptTpCd" :label="item.optTp.nmLan + \' - \' + item.smenuOptTpNm" :value="item.smenuOptTpCd">' +
				'		<span>{{ item.optTp.nmLan + " - " + item.smenuOptTpNm }}</span>' +
				'	</el-option>' +
				'</el-select>',
	methods: {
		handleChange: function (val) {
			this.$emit('change', val);
		}
	}
});

Vue.component('opt-combo', {
	props: ['model', 'data'],
	template: 	'<el-select v-model="modelVal" multiple @change="handleChange" style="width: 100%;">' +
				'	<el-option v-for="item in data" :key="item.smenuOptCd" :label="item.smenuOptNm + \' - \' + item.smenuOptTp.smenuOptTpNm" :value="item.smenuOptCd">' +
				'		<span>{{ item.smenuOptNm + " - " + item.smenuOptTp.smenuOptTpNm }}</span>' +
				'	</el-option>' +
				'</el-select>',
	computed: {
		modelVal: function () {
			var tmpVal = [];
			
			if (null != this.model) {
				for (var i = 0, len = this.model.length; i < len; i++) {
					tmpVal.push(this.model[i].smenuOptCd);
				}
			}
			
			console.log("=============tmpVal:" + tmpVal);
			
			return tmpVal;
		}
	},
	methods: {
		handleChange: function (val) {
			var returnVal = [];
			
			console.log("=============val:" + val);
			
			for (var i = 0, len = this.data.length; i < len; i++) {
				for (var j = 0, jlen = val.length; j < jlen; j++) {
					if (this.data[i].smenuOptCd == val[j]) {
						returnVal.push(this.data[i]);
					}
				}
			}
			
			console.log("=============returnVal:" + returnVal);
			
			this.$emit('change', returnVal);
		}
	}
});

Vue.component('stuff-combo', {
	props: ['model', 'data'],
	template: 	'<el-select v-model="modelVal" multiple @change="handleChange" style="width: 100%;">' +
				'	<el-option v-for="item in data" :key="item.stuffCd" :label="item.stuffNm" :value="item.stuffCd">' +
				'		{{ item.stuffNm }} ' +
				'	</el-option>' +
				'</el-select>',
	computed: {
		modelVal: function () {
			var tmpVal = [];
			
			if (null != this.model) {
				for (var i = 0, len = this.model.length; i < len; i++) {
					tmpVal.push(this.model[i].stuffCd);
				}
			}
			return tmpVal;
		}
	},
	methods: {
		handleChange: function (val) {
			var returnVal = [];
			
			for (var i = 0, len = this.data.length; i < len; i++) {
				for (var j = 0, jlen = val.length; j < jlen; j++) {
					if (this.data[i].stuffCd == val[j]) {
						returnVal.push(this.data[i]);
					}
				}
			}
			
			this.$emit('change', returnVal);
		}
	}
});

Vue.component('discount-combo', {
	props: ['model', 'data'],
	template: 	'<el-select v-model="modelVal" multiple @change="handleChange" style="width: 100%;">' +
				'	<el-option v-for="item in data" :key="item.discountCd" :label="item.discountNm" :value="item.discountCd"></el-option>' +
				'</el-select>',
	computed: {
		modelVal: function () {
			var tmpVal = [];
			
			if (null != this.model) {
				for (var i = 0, len = this.model.length; i < len; i++) {
					tmpVal.push(this.model[i].discountCd);
				}
			}
			return tmpVal;
		}
	},
	methods: {
		handleChange: function (val) {
			var returnVal = [];
			
			for (var i = 0, len = this.data.length; i < len; i++) {
				for (var j = 0, jlen = val.length; j < jlen; j++) {
					if (this.data[i].discountCd == val[j]) {
						returnVal.push(this.data[i]);
					}
				}
			}
			
			this.$emit('change', returnVal);
		}
	}
});

Vue.component('delivery-tag', {
	props:['model', 'data', 'cost'],
	template: `<div class="tag-group"> 
			  		<el-form-item label="배달비"> 
			  			<span class="tag-group__title">{{ cost }}</span> 
			  		</el-form-item> 
			  		<el-form-item label="배달 지역"> 
			  			<el-tag 
			  				:key="tag" 
			  				v-for="tag in data" 
			  				closable 
			  				:disable-transitions="false" 
			  				:type="'info'" 
			  				@close="handleClose(tag)"> 
			  				{{tag.addressBub}}({{tag.addressHeng}})
			  			</el-tag> 
			  		</el-form-item> 
			  </div>
				`,
    methods:{
        handleClose(tag) {
            this.model.splice(this.model.indexOf(tag), 1);
        },
    },
});

Vue.component('delivery-memo',{
	props:['model','data'],
	template:`<div>
			 	<el-form-item label="특이사항">
			 	<el-input
			 		type="textarea"
			 		:rows="2"
			 		placeholder="특이사항 입력"
			 		@change="insMemo"
			 		v-model="model">
			 	</el-input>
			 	</el-form-item>
			 </div>
			 `,
	methods:{
		insMemo: function () {
			this.$emit('change', this.data, this.model);
		}
	}
});