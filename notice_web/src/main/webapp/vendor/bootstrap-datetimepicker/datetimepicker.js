/*

This datetimepicker is a simple angular wrapper of bootstrap datetimepicker(https://github.com/smalot/bootstrap-datetimepicker), which is the best I could found so far. 
It depends on the following stuffs:
1. bootstrap.css 2 or 3 
2. bootstrap-datetimepicker.css
3. jquery.js
4. bootstrap.js
5. bootstrap-datetimepicker.js
6. angular.js

Sample:
	<datetimepicker ng-model='date' today-btn='true' minute-step='30' ></datetimepicker>

Ron Liu
5/2/2014

*/

angular.module('angularjs-bootstrap-datetimepicker', [])

.directive('datetimepicker', function () {
	function _byDefault(value, defaultValue) {
		return _isSet(value) ? value : defaultValue;
		function _isSet(value) {
			return !(value === null || value === undefined || value === NaN || value === '');
		}
	}

	return {
		restrict: 'E',
		scope: {
			ngModel: '=',
			format: '@',
			todayBtn: '@',
			weekStart: '@',
			minuteStep: '@',
			language: '@',
			zIndex: '@'
		},
		template:
			'<div class="input-append date form_datetime">' +
	        '   <input class="form-control" style="border-radius:0;background-color: #fff;padding-right: 0;" size="16" type="text" value="" readonly>' +
	        '   <span class="add-on"><i class="icon-remove"></i></span>' +
			'	<span class="add-on"><i class="icon-th"></i></span>' +
	        '</div>',

		link: function (scope, element, attrs) {
			var $element = $(element.children()[0]);

			$element.datetimepicker({
				format: _byDefault(scope.format, 'yyyy-mm-dd hh:ii'),
				weekStart: _byDefault(scope.weekStart, '1'),
				todayBtn: _byDefault(scope.todayBtn, 'true') === 'true',
				minuteStep: parseInt(_byDefault(scope.minuteStep, '5')),
				language: scope.language,
				autoclose: 1,
				todayHighlight: 1,
				startView: 2,
				forceParse: 0,
				showMeridian: 0,
				zIndex: scope.zIndex
			})
        	.on('changeDate', function (ev) {
        		scope.$apply(function() {
        			scope.ngModel = ev.date;
        		});
        	});

			scope.$watch('ngModel', function (newValue, oldValue) {
				$element.datetimepicker('update', newValue);
			});
		}
	};
});