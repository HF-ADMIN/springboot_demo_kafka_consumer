# demo_tinfo yaml파일 사용 가이드

### 'demo-tinfo' namespace가 미리 생성되어 있어야 한다.
1. deployment 생성
	1) kafka_consumer-deploy 생성
		- seoul timezone 설정합니다.
		- 위에서 생성한 pvc에 대한 설정합니다.
		- spec.template.spec.containers.image에 사용하는 도커계정을 입력합니다.(이 부분은 사용자가 변경 필요)
		
	
3. service 생성
	1) kafka_consumer 생성
		- NodePort 타입으로 서비스를 생성합니다.
		- 서비스의 nodePort는 '30698'로 설정합니다.
