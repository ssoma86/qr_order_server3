var axiosUtil = {
	axios_: null,
	vue_: null,
	loading_: null,

	// Axios 초기화
	init: function (vue) {
		this.vue_ = vue;
		this.axios_ = axios.create({
			timeout: 30 * 1000,
			validateStatus: function (status) {
				return status >= 200; // 서버에서 200이상 메세지이면 전부 표시
			}
		});

		return this;
	},
	// 일괄 오류 처리 부분
	axiosResponse: function (response, fun, error) {
		let data = response.data;

		if (response.status != 200 && response.status != 400 && response.status != 403 && response.status != 408) {

//				if (util.isEmpty(data.message)) {
//					data.message = 'Server Error !!'
//				}

			this.vue_.$message({
				type: 'warning',
				duration: 6000,
				showClose: true,
				message: data.message
			});
		}

		switch (response.status) {
			case 200:
				fun(data);
				break;
			case 400:
				for (let i = 0, len = data.errorsMessage.length; i < len; i++) {
					if (error.hasOwnProperty(data.errorsMessage[i].field)) {
						error[data.errorsMessage[i].field] = data.errorsMessage[i].errorMsg;
					}
				}
				break;
			case 403: // 권한 체크
				this.vue_.$message({
					type: 'warning',
					duration: 6000,
					showClose: true,
					message: this.vue_.appMsg.systemAlterNoPermissionsMsg
				});
				break;
			case 404:
				break;
			case 405:
				break;
			case 408: // Timeout 처리
//				this.vue_.$confirm(this.vue_.appMsg.systemAlterTimeoutMsg, this.vue_.appMsg.systemAlterTitle, {
//					type: 'warning',
//					confirmButtonText: this.vue_.appMsg.systemAlterButton,
//					showCancelButton: false
//				}).then(function () {
				location.href = '/';
//				});
				break;
			case 500:
				break;
			default:
				break;
		}

		this.vue_.closeLoading();
	},
	// 조회
	search: function (url, data, fun) {
		this.vue_.showLoading();

		this.axios_.get(url, { params: data })
			.then(function (response) { this.axiosResponse(response, fun); }.bind(this))
			.catch(function (error) { this.vue_.closeLoading(); }.bind(this));
	},
	// 하나 조회
	get: function (url, fun) {
		this.vue_.showLoading();

		this.axios_.get(url)
			.then(function (response) { this.axiosResponse(response, fun); }.bind(this))
			.catch(function (error) { this.vue_.closeLoading(); }.bind(this));
	},
	// 추가
	add: function (url, data, error, fun) {
		this.vue_.showLoading();

		this.axios_.post(url, data)
			.then(function (response) {
				this.axiosResponse(response, fun, error);
			}.bind(this))
			.catch(function (error) { this.vue_.closeLoading(); }.bind(this));
	},
	// 조회
//	up: function (url, data, error, fun) {
//		this.vue_.showLoading();
//
//		this.axios_.get(url, data)
//			.then(function (response) {
//				this.axiosResponse(response, fun, error);
//			}.bind(this))
//			.catch(function (error) { this.vue_.closeLoading(); }.bind(this));
//	},
	// 삭제
	del: function (url, fun) {
		this.vue_.showLoading();

		this.axios_.delete(url)
			.then(function (response) {
				this.axiosResponse(response, fun);
			}.bind(this))
			.catch(function (error) { this.vue_.closeLoading(); }.bind(this));
	},
	// del: function (url, fun) {
	// 	this.vue_.showLoading();
		
	// 	this.axios_.get(url)
	// 	.then(function (response) {
	// 		this.axiosResponse(response, fun);
	// 	}.bind(this))
	// 	.catch(function (error) { this.vue_.closeLoading(); }.bind(this));
	// },
	
	// 수정
	up: function (url, data, error, fun) {
		this.vue_.showLoading();

		this.axios_.put(url, data)
			.then(function (response) {
				this.axiosResponse(response, fun, error);
			}.bind(this))
			.catch(function (error) { this.vue_.closeLoading(); }.bind(this));
	},
	// up: function (url, data, error, fun) {
	// 	this.vue_.showLoading();
		
	// 	this.axios_.post(url, data)
	// 	.then(function (response) {
	// 		this.axiosResponse(response, fun, error);
	// 	}.bind(this))
	// 	.catch(function (error) { this.vue_.closeLoading(); }.bind(this));
	// },
	// 수정
	patchNonLoading: function (url, data, error, fun) {

		this.axios_.patch(url, data)
			.then(function (response) {
				this.axiosResponse(response, fun, error);
			}.bind(this))
			.catch(function (error) { }.bind(this));
	},
	// POST
	post: function (url, data, fun) {
		this.vue_.showLoading();

		this.axios_.post(url, data)
			.then(function (response) {
				this.axiosResponse(response, fun);
			}.bind(this))
			.catch(function (error) { this.vue_.closeLoading(); }.bind(this));
	}


}
