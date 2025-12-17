// Custom Swagger UI Script - Restaurant Portal
console.log('✅ Custom JS loaded for Restaurant Portal');

const binder = setInterval(() => {
    try {
        const logoLink = document.querySelector("#swagger-ui a.link");
        const logoImg = document.querySelector("#swagger-ui a.link > svg");
        if (logoImg) {
            logoImg?.remove();
            const img = document.createElement('img')
            img.setAttribute("src", "/api/logo-white.svg")
            logoLink.append(img);
            clearInterval(binder);
        }
    }
    catch { }
}, 100)

document.addEventListener('DOMContentLoaded', function () {

    let link = document.querySelector("link[rel*='icon']") || document.createElement('link');
    if (link.parentNode) {
        document.head.removeChild(link);
    }
    link = document.querySelector("link[rel*='icon']") || document.createElement('link');
    if (link.parentNode) {
        document.head.removeChild(link);
    }
    link = document.createElement('link');
    link.type = 'image/x-icon';
    link.rel = 'shortcut icon';
    link.href = '/api/favicon.ico';
    document.getElementsByTagName('head')[0].appendChild(link);

    let title = document.querySelector("title") || document.createElement('title');
    if (title.parentNode) {
        document.head.removeChild(title);
    }

    title = document.querySelector("title") || document.createElement('title');
    title.text = "RESTAURANT PORTAL - HỆ THỐNG QUẢN LÝ NHÀ HÀNG";
    document.getElementsByTagName('head')[0].appendChild(title);


    // Inject overlay + login modal
    const customLoginModal = `
        <div id="custom-login-overlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background-color:rgba(0,0,0,0.5); z-index:9998;"></div>
        <div id="custom-login" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%, -50%); width:400px; background:#fff; border-radius:8px; box-shadow:0 0 10px rgba(0,0,0,0.3); padding:30px; z-index:9999;">
            <h2 style="margin-bottom:20px; text-align:center;">ĐĂNG NHẬP</h2>
            <div style="margin-bottom:15px;">
                <label>Tên đăng nhập</label>
                <input id="username" placeholder="Nhập tên đăng nhập" value="admin" style="width:100%; padding:10px; margin-top:5px; border:1px solid #ccc; border-radius:4px;">
            </div>
            <div style="margin-bottom:15px;">
                <label>Mật khẩu</label>
                <input id="password" type="password" placeholder="Nhập mật khẩu" value="abc@123" style="width:100%; padding:10px; margin-top:5px; border:1px solid #ccc; border-radius:4px;">
            </div>
            <div style="text-align:center;">
                <button id="loginBtn" style="padding:10px 20px; background:#1eb181; color:white; border:none; border-radius:4px; margin-right:10px;">Đăng nhập</button>
                <button id="closeBtn" style="padding:10px 20px; background:#ccc; color:black; border:none; border-radius:4px;">Đóng</button>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', customLoginModal);

    function showLoginModal() {
        document.getElementById('custom-login').style.display = 'block';
        document.getElementById('custom-login-overlay').style.display = 'block';
        document.getElementById('username').focus();
    }
    async function login() {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const now = new Date();

        const pad = (num, size) => String(num).padStart(size, '0');

        const year = now.getFullYear();
        const month = pad(now.getMonth() + 1, 2);
        const day = pad(now.getDate(), 2);
        const hours = pad(now.getHours(), 2);
        const minutes = pad(now.getMinutes(), 2);
        const seconds = pad(now.getSeconds(), 2);
        const milliseconds = pad(now.getMilliseconds(), 3);
        const clientId = `${year}${month}${day}${hours}${minutes}${seconds}${milliseconds}`;
        try {
            const res = await fetch('/api/portal/auth/login', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'CLIENT_ID': clientId
                 },
                credentials: 'omit',
                body: JSON.stringify({ username, password })
            });

            if (!res.ok) {
                const errorData = await res.json();
                throw new Error(errorData.message || "Đăng nhập không thành công");
            }

            const data = await res.json();
            if (data.data && data.data.accessToken) {
                window.ui.preauthorizeApiKey("Bearer", "Bearer " + data.data.accessToken);
                window.ui.getConfigs().requestInterceptor = (req) => {
                    req.headers["CLIENT_ID"] = clientId;
                    return req;
                };
                hideLoginModal();
            }
            else {
                alert(data.message || "Đăng nhập không thành công");
            }
        } catch (err) {
            alert(err.message || err);
        }
    }
    function hideLoginModal() {
        document.getElementById('custom-login').style.display = 'none';
        document.getElementById('custom-login-overlay').style.display = 'none';
    }

    document.getElementById('loginBtn').addEventListener('click', login);
    document.getElementById('username').addEventListener('keypress', async function (e) {
        if (e.key === 'Enter') {
            await login();
        }
    });
    document.getElementById('password').addEventListener('keypress', async function (e) {
        if (e.key === 'Enter') {
            await login();
        }
    });

    document.getElementById('closeBtn').addEventListener('click', hideLoginModal);
    document.getElementById('custom-login-overlay').addEventListener('click', hideLoginModal);

    function tryAddLoginButton(retryCount = 0) {
        // Thử nhiều selector khác nhau
        const selectors = [
            '.swagger-ui .download-url-wrapper',
            '.swagger-ui .topbar-wrapper',
            '.swagger-ui .topbar',
            '.swagger-ui .info',
            '.swagger-ui .scheme-container',
            '.swagger-ui .info-container',
            '#swagger-ui .topbar-wrapper',
            '.swagger-ui .wrapper'
        ];

        let targetElement = null;
        for (const selector of selectors) {
            targetElement = document.querySelector(selector);
            if (targetElement) {
                console.log('Found element with selector:', selector);
                break;
            }
        }

        // Nếu không tìm thấy, thử tìm bất kỳ element nào trong swagger-ui
        if (!targetElement) {
            const swaggerUi = document.querySelector('#swagger-ui') || document.querySelector('.swagger-ui');
            if (swaggerUi) {
                // Tìm element đầu tiên có class chứa "wrapper" hoặc "topbar"
                targetElement = swaggerUi.querySelector('.topbar-wrapper') || 
                               swaggerUi.querySelector('.download-url-wrapper') ||
                               swaggerUi.querySelector('.info') ||
                               swaggerUi.firstElementChild;
            }
        }

        if (targetElement && !document.getElementById('openLoginForm')) {
            const loginButton = document.createElement("button");
            loginButton.innerText = "Đăng nhập";
            loginButton.type = "button";
            loginButton.id = "openLoginForm";
            loginButton.style.margin = "0px 5px";
            loginButton.style.padding = "8px 16px";
            loginButton.style.background = "#1eb181";
            loginButton.style.color = "#fff";
            loginButton.style.border = "none";
            loginButton.style.borderRadius = "4px";
            loginButton.style.cursor = "pointer";
            loginButton.style.zIndex = "9999";
            loginButton.style.fontSize = "14px";
            loginButton.style.fontWeight = "bold";

            loginButton.addEventListener("click", showLoginModal);

            // Thử append vào targetElement, nếu không được thì prepend
            try {
                targetElement.appendChild(loginButton);
                console.log('Login button added successfully');
            } catch (e) {
                try {
                    targetElement.insertBefore(loginButton, targetElement.firstChild);
                    console.log('Login button prepended successfully');
                } catch (e2) {
                    // Nếu cả hai cách đều fail, thêm vào body
                    loginButton.style.position = "fixed";
                    loginButton.style.top = "10px";
                    loginButton.style.right = "10px";
                    document.body.appendChild(loginButton);
                    console.log('Login button added to body');
                }
            }
        } else if (!targetElement) {
            if (retryCount < 50) {
                setTimeout(() => tryAddLoginButton(retryCount + 1), 300);
            } else {
                console.warn('Could not find target element for login button after 50 retries');
                // Thêm button vào body như fallback
                if (!document.getElementById('openLoginForm')) {
                    const loginButton = document.createElement("button");
                    loginButton.innerText = "Đăng nhập";
                    loginButton.type = "button";
                    loginButton.id = "openLoginForm";
                    loginButton.style.position = "fixed";
                    loginButton.style.top = "10px";
                    loginButton.style.right = "10px";
                    loginButton.style.padding = "8px 16px";
                    loginButton.style.background = "#1eb181";
                    loginButton.style.color = "#fff";
                    loginButton.style.border = "none";
                    loginButton.style.borderRadius = "4px";
                    loginButton.style.cursor = "pointer";
                    loginButton.style.zIndex = "9999";
                    loginButton.style.fontSize = "14px";
                    loginButton.style.fontWeight = "bold";
                    loginButton.addEventListener("click", showLoginModal);
                    document.body.appendChild(loginButton);
                    console.log('Login button added to body as fallback');
                }
            }
        }

        const swaggerContainer = document.querySelector('.scheme-container');
        if (swaggerContainer) {
            swaggerContainer.remove();
        }
    }

    // Sử dụng MutationObserver để theo dõi khi Swagger UI render
    const observer = new MutationObserver((mutations, obs) => {
        if (document.querySelector('#swagger-ui') || document.querySelector('.swagger-ui')) {
            tryAddLoginButton();
            // Nếu đã tìm thấy và thêm button, có thể dừng observer
            if (document.getElementById('openLoginForm')) {
                obs.disconnect();
            }
        }
    });

    // Bắt đầu observe
    observer.observe(document.body, {
        childList: true,
        subtree: true
    });

    // Cũng thử ngay lập tức
    tryAddLoginButton();
});


function bindClientIdInterceptor() {
    if (window.ui && window.ui.getConfigs) {
        const now = new Date();
        const pad = (num, size) => String(num).padStart(size, '0');
        const clientId = `${now.getFullYear()}${pad(now.getMonth() + 1, 2)}${pad(now.getDate(), 2)}${pad(now.getHours(), 2)}${pad(now.getMinutes(), 2)}${pad(now.getSeconds(), 2)}${pad(now.getMilliseconds(), 3)}`;

        const oldInterceptor = window.ui.getConfigs().requestInterceptor;
        window.ui.getConfigs().requestInterceptor = (req) => {
            if (!req.headers["CLIENT_ID"]) {
                req.headers["CLIENT_ID"] = clientId;
            }
            if (oldInterceptor) {
                req = oldInterceptor(req);
            }
            return req;
        };
        console.log("✅ CLIENT_ID interceptor attached:", clientId);
    } else {
        setTimeout(bindClientIdInterceptor, 300); // thử lại sau 0.3s
    }
}

bindClientIdInterceptor();

