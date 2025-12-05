package org.mizoguchi.misaki.common.constant;

public class EmailConstant {
    public static final String SENDER_FRIENDLY_NAME = "Misaki";
    public static final String EMAIL_ENCODING = "UTF-8";
    public static final String VERIFICATION_EMAIL_SUBJECT = "Misaki - 电子邮箱验证码";
    public static final String LOGO_CONTENT_ID = "logoImage";
    public static final String VERIFICATION_EMAIL_BODY = """
                    <html lang="zh">
                    
                    <head>
                        <title>Misaki 验证码</title>
                        <meta charset="utf-8" />
                        <meta name="viewport" content="width=device-width, initial-scale=1" />
                        <style>
                            p {
                                margin: 0;
                            }
                    
                            hr {
                                border: 1px solid #c1c1c1;
                                margin: 24px 0;
                            }
                        </style>
                    </head>
                    
                    <body>
                        <div style="font-family: system-ui;">
                            <div
                                style="max-width: 742px; margin: 0 auto; padding: 24px; background-color: #f5f5f7; color: #1d1d1f; border-radius: 24px;">
                                <div style="display: flex; align-items: center; gap: 6px;">
                                    <img src="cid:logoImage" alt="logo" style="height:42px; fill: #3142ef;" />
                                    <span style="font-size: 36px; font-weight: 600;">Misaki</span>
                                </div>
                                <hr>
                                <div>
                                    <p style="font-size: 36px; font-weight: 600; margin-bottom: 12px;">
                                        <span>你的验证码是：</span>
                                        <span style='color:#3142ef;'>%s</span>
                                    </p>
                                    <p>有效期5分钟，请尽快使用。</p>
                                </div>
                                <hr>
                                <div style="color: #888; font-size: small;">
                                    <p>你收到此电子邮件的原因：</p>
                                    <p>当你使用电子邮箱注册 Misaki 账户或者更改 Misaki 账户密码时，Misaki 会提出验证要求。</p>
                                    <p>如果你未做过此更改，或者认为有人未经授权访问了你的账户，你需尽快前往你的 Misaki 账户页面更改你的密码。</p>
                                </div>
                            </div>
                        </div>
                    </body>
                    
                    </html>
                    """;
}
