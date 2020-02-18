# WeChat Pay Payments with Sources API on Android

## Overview
This app demonstrates
1. Creating a Stripe Source for WeChat using the Sources API
2. Integrate with the WeChat SDK to capture payment and complete the transaction

See [WeChat Pay Payments with Sources](https://stripe.com/docs/sources/wechat-pay) for more details.

## Setup

### Requirements
- Your app must be registered [WeChat Open Platform](https://open.weixin.qq.com/).
- You must have a WeChat account with WeChat Pay enabled.

### Configure
1. Create `~/.gradle/gradle.properties` if it doesn't exist
2. Add the following entries to the end of the file and set to the appropriate values
   ```
   STRIPE_WECHAT_EXAMPLE_APP_ID=wx123
   STRIPE_WECHAT_EXAMPLE_APP_SIGNATURE=123456789
   STRIPE_WECHAT_EXAMPLE_STRIPE_KEY=pk_live_mykey
   ```

### Run
1. Clone the `wechatpay-sources-android` repository.
2. Open the project in Android Studio.
3. Run the `app` project.

## Demo
<img width="320" height="640" src="https://raw.githubusercontent.com/stripe-samples/wechatpay-sources-android/master/assets/demo.gif" />
