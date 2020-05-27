package com.example.notificationsync

import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


object MailUtils {
    fun sendMail(
        title: String,
        receiver: String,
        sender: String,
        content: String,
        mailAccount: String,
        password: String,
        smtpServer: String,
        port: Int
    ) {
        Thread {
            //            properties["mail.smtp.port"] = 465;// 端口号
            //            properties["mail.smtp.host"] = "smtp.qq.com";// 主机名


            val properties = Properties()
            properties["mail.transport.protocol"] = "smtp"// 连接协议
            properties["mail.smtp.host"] = smtpServer// 主机名
            properties["mail.smtp.port"] = port// 端口号
            properties["mail.smtp.auth"] = "true"
            properties["mail.smtp.ssl.enable"] = "true"// 设置是否使用ssl安全连接 ---一般都使用
            properties["mail.debug"] = "true"// 设置是否显示debug信息 true 会在控制台显示相关信息

            // 得到回话对象
            val session: Session = Session.getInstance(properties)
            // 获取邮件对象
            val message: Message = MimeMessage(session)
            // 设置发件人邮箱地址
            message.setFrom(InternetAddress(sender))
            // 设置收件人邮箱地址
            message.setRecipients(
                Message.RecipientType.TO,
                arrayOf(
                    InternetAddress(receiver)
//                InternetAddress("xxx@qq.com")
                )
            )
//message.setRecipient(Message.RecipientType.TO, new InternetAddress("xxx@qq.com"));//一个收件人
// 设置邮件标题
            message.subject = title
            // 设置邮件内容
            message.setText(content)
            // 得到邮差对象
            val transport: Transport = session.transport
            // 连接自己的邮箱账户
            try {
                transport.connect(mailAccount, password) // 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
                // 发送邮件
                transport.sendMessage(message, message.allRecipients)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                transport.close()
            }
        }.start()
    }
}