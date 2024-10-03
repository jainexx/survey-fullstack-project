<script>
export default {
    data() {
        return {
            isRightPanelActive: false,
        };
    },
    methods: {
        togglePanel(isSignUp) {
            this.isRightPanelActive = isSignUp;
        },
        onSubmit() {
            // 處理表單提交邏輯
        }
    }
};
</script>

<template>
    <div class="pageContainer">
        <header>
            <div class="logo">
                <!-- <img src="path/to/logo.png" alt="VoxPoP Logo"> -->
                <span>VoxPoP</span>
            </div>
            <div class="joinUs">
                <span>Join Us</span>
                <span>NOW</span>
            </div>
        </header>
        <div class="container" :class="{ 'rightPanelActive': isRightPanelActive }">
            <div class="formContainer signUpContainer">
                <form @submit.prevent="onSubmit">
                    <h1>Create a Account</h1>
                    <div class="socialContainer">
                        <a href="#" class="social"><i class="fab fa-facebook-f"></i></a>
                        <a href="#" class="social"><i class="fab fa-google-plus-g"></i></a>
                        <a href="#" class="social"><i class="fab fa-twitter"></i></a>
                        <a href="#" class="social"><i class="fab fa-instagram"></i></a>
                    </div>
                    <input type="text" placeholder="姓名" />
                    <input type="email" placeholder="信箱" />
                    <input type="password" placeholder="密碼" />
                    <button type="submit">註冊</button>
                </form>
            </div>
            <div class="formContainer signInContainer">
                <form @submit.prevent="onSubmit">
                    <h1>Sign In</h1>
                    <div class="socialContainer">
                        <a href="#" class="social"><i class="fab fa-facebook-f"></i></a>
                        <a href="#" class="social"><i class="fab fa-google-plus-g"></i></a>
                        <a href="#" class="social"><i class="fab fa-twitter"></i></a>
                        <a href="#" class="social"><i class="fab fa-instagram"></i></a>
                    </div>
                    <input type="email" placeholder="信箱" />
                    <input type="password" placeholder="密碼" />
                    <a href="#">忘記密碼？</a>
                    <button type="submit" @click="this.$router.push('/authuser')">登入</button>
                </form>
            </div>
            <div class="overlayContainer">
                <div class="overlay">
                    <div class="overlayPanel overlayLeft">
                        <h1>Welcome Back!</h1>
                        <p>已有帳戶？立即登入查看最新消息</p>
                        <button class="ghost" id="signIn" @click="togglePanel(false)">登入</button>
                    </div>
                    <div class="overlayPanel overlayRight">
                        <h1>Hello Stranger!</h1>
                        <p>註冊帳號完善你的個人問卷管理</p>
                        <button class="ghost" id="signUp" @click="togglePanel(true)">註冊</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
body {
    background: $bgColor;
    display: flex;
    justify-content: center;
    align-items: center;
    font-family: 'Ubuntu', sans-serif;
    margin: 0;
    padding: 0;
}

.pageContainer {
    width: 100%;
    max-width: 1200px;
    margin: auto;
}

header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 40px;
    background-color: $bgColor;

    .logo {
        display: flex;
        align-items: center;

        img {
            width: 50px;
            height: 50px;
            margin-right: 10px;
        }

        span {
            font-size: 24px;
            font-weight: bold;
            color: $black;
        }
    }

    .joinUs {
        font-size: 36px;
        font-weight: bold;
        text-align: right;
        color: $black;

        span {
            display: block;

            &:first-of-type {
                font-size: 48px;
                color: $orange;
            }

            &:last-of-type {
                font-size: 36px;
                font-style: italic;
            }
        }
    }
}

.container {
    background-color: $bgColor;
    border-radius: 20px;
    box-shadow: 0 14px 28px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22);
    position: relative;
    overflow: hidden;
    width: 900px;
    max-width: 100%;
    min-height: 600px;
    display: flex;

    &.rightPanelActive .signInContainer {
        transform: translateX(100%);
    }

    &.rightPanelActive .signUpContainer {
        transform: translateX(100%);
        opacity: 1;
        z-index: 5;
        animation: show 0.6s;
    }

    &.rightPanelActive .overlayContainer {
        transform: translateX(-100%);
    }

    &.rightPanelActive .overlay {
        transform: translateX(50%);
    }

    &.rightPanelActive .overlayLeft {
        transform: translateX(0);
    }

    &.rightPanelActive .overlayRight {
        transform: translateX(20%);
    }
}

.formContainer {
    position: absolute;
    top: 0;
    height: 100%;
    width: 50%;
    transition: all 0.6s ease-in-out;
    z-index: 2;

    &.signInContainer {
        left: 0;
        background-color: $bgColor;
        padding: 60px;
        border-radius: 20px 0 0 20px;
    }

    &.signUpContainer {
        left: 0;
        opacity: 0;
        z-index: 1;
        padding: 60px;
        border-radius: 0 20px 20px 0;
    }

    form {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;

        h1 {
            font-size: 28px;
            margin-bottom: 20px;
            color: $black;
            z-index: 3;
            position: relative;
        }

        .socialContainer {
            display: flex;
            justify-content: center;
            margin-bottom: 20px;
            z-index: 3;
            position: relative;

            a {
                border: 1px solid $black;
                border-radius: 50%;
                display: inline-flex;
                justify-content: center;
                align-items: center;
                margin: 0 10px;
                height: 40px;
                width: 40px;
                color: $black;

                i {
                    font-size: 18px;
                }
            }
        }

        input {
            background-color: $green;
            border: none;
            padding: 15px;
            margin: 10px 0;
            width: 100%;
            border-radius: 5px;
            color: $black;
            font-size: 16px;
            z-index: 3;
            position: relative;
        }

        button {
            border-radius: 10px;
            border: none;
            background-color: $orange;
            color: #FFFFFF;
            font-size: 14px;
            font-weight: bold;
            padding: 12px 45px;
            margin-top: 20px;
            text-transform: uppercase;
            transition: transform 80ms ease-in;
            z-index: 3;
            position: relative;

            &:active {
                transform: scale(0.95);
            }

            &:focus {
                outline: none;
            }

            &.ghost {
                background-color: transparent;
                border: 2px solid $orange;
                color: $orange;
            }
        }

        a {
            color: $black;
            font-size: 14px;
            text-decoration: none;
            margin-top: 10px;
            z-index: 3;
            position: relative;
        }
    }
}

.overlayContainer {
    position: absolute;
    top: 0;
    left: 50%;
    width: 50%;
    height: 100%;
    overflow: hidden;
    transition: transform 0.6s ease-in-out;
    z-index: 100;
}

.overlay {
    background-color: $purple;
    background-repeat: no-repeat;
    background-size: cover;
    background-position: 0 0;
    color: #FFFFFF;
    position: relative;
    left: -100%;
    height: 100%;
    width: 200%;
    transform: translateX(0);
    transition: transform 0.6s ease-in-out;

    &Panel {
        position: absolute;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        padding: 0 40px;
        text-align: center;
        top: 0;
        height: 100%;
        width: 50%;
        transform: translateX(0);
        transition: transform 0.6s ease-in-out;

        &.overlayLeft {
            transform: translateX(-20%);
        }

        &.overlayRight {
            right: 0;
            transform: translateX(0);
        }
    }
}

@keyframes show {
    0%, 49.99% {
        opacity: 0;
        z-index: 1;
    }
    
    50%, 100% {
        opacity: 1;
        z-index: 5;
    }
}
</style>





