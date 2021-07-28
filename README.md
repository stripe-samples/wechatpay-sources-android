# WeChat Pay Payments with PaymentIntent API on Android

## Overview
This app demonstrates
1. Creating a Stripe PaymentIntent for WeChat using the PaymentIntent API
2. Integrating with the WeChat SDK to capture payment and complete the transaction through stripe-wechatpay module

See [Accept a WeChat Pay payment](https://stripe.com/docs/payments/wechat-pay/accept-a-payment) for more details.

## Setup

### Requirements
- Your app must be registered [WeChat Open Platform](https://open.weixin.qq.com/) and have a appId assigned by WeChat.
- You must have a WeChat account with WeChat Pay enabled.
- See more details of Stripe's WeChat Pay module [here](https://github.com/stripe/stripe-android/blob/master/wechatpay/README.md).

### Deploy the example backend to Heroku
1. [Create a Heroku account](https://signup.heroku.com/) if you don't have one.
2. Navigate to the [example mobile backend repo](https://github.com/stripe/example-mobile-backend/tree/v19.0.0)
   and click "Deploy to Heroku".
3. Set an _App Name_ of your choice (e.g. Stripe Example Mobile Backend).
4. Under _Config Vars_, set your [Stripe livemode secret key](https://dashboard.stripe.com/test/apikeys)
   for the `STRIPE_TEST_SECRET_KEY` field.
   > A live key is required for valid WeChat Pay parameters.
5. Click "Deploy for Free".

<img width="700" height="793" src="https://raw.githubusercontent.com/stripe/stripe-android/master/example/images/heroku.png" />

### Configure
1. Create `~/.gradle/gradle.properties` if it doesn't exist
2. Add the following entries to the end of the file and set to the appropriate values
   ```
   STRIPE_WECHAT_EXAMPLE_APP_ID=wx123456
   STRIPE_WECHAT_EXAMPLE_STRIPE_KEY=pk_live_mykey
   STRIPE_WECHAT_EXAMPLE_BACKEND_URL=https://my-backend.herokuapp.com/
   ```
   > STRIPE_WECHAT_EXAMPLE_APP_ID and STRIPE_WECHAT_EXAMPLE_STRIPE_KEY are used to talk to Stripe backend to create a PaymentIntent. A live key is required for valid WeChat Pay parameters.

### Run
1. Clone the `wechatpay-sources-android` repository.
2. Open the project in Android Studio.
3. Run the `app` project.

## Demo
<img width="320" height="640" src="https://user-images.githubusercontent.com/79880926/127387035-762bc3e9-b570-4a2a-bbf7-413ae24ed501.gif" />
