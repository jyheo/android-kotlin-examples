from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy


class TestAppium():  
    def __init__(self, appLocation, appium_server_url = 'http://127.0.0.1:4723') -> None:
        options = UiAutomator2Options()
        options.platform_name = 'Android'
        options.automation_name = 'UiAutomator2'
        options.app = appLocation   # path to the app package or use app_package and app_activity instead
        #options.allow_test_packages = True
        options.enforce_app_install = True
        options.uiautomator2_server_install_timeout = 20000
        options.adb_exec_timeout = 20000
        options.language = 'en'
        options.locale = 'US'
        #options.auto_grant_permissions = True

        self.driver = webdriver.Remote(appium_server_url, options=options)
        self.driver.implicitly_wait(10)


    def test_myapp(self) -> None:
        try:
            txtview = self.driver.find_element(AppiumBy.XPATH, '//android.widget.TextView[@text="Hello Android!"]')
            #txtview = self.driver.find_element(AppiumBy..ID, 'txtView')
            print(txtview.text)
            return 'OK'            
        except:
            return 'No TextView with "Hello Android!" text found.'


if __name__ == '__main__':
    # 테스트할 APK 파일의 위치
    APP_LOCATION = r'C:\Users\jyheo\AndroidStudioProjects\MyApplication\app\build\outputs\apk\debug\app-debug.apk'

    testApp = TestAppium(APP_LOCATION)
    r = testApp.test_myapp()
    print(r)
