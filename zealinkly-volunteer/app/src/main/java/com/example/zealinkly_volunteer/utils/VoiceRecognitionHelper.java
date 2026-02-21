package com.example.zealinkly_volunteer.utils;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VoiceRecognitionHelper {
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private Context context;
    
    public VoiceRecognitionHelper(Context context) {
        this.context = context;
    }
    
    /**
     * 开始录制音频
     * @return 是否成功开始录制
     */
    public boolean startRecording() {
        try {
            // 创建临时文件
            File audioFile = File.createTempFile("audio", ".mp4", context.getCacheDir());
            audioFilePath = audioFile.getAbsolutePath();
            
            // 初始化MediaRecorder
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFilePath);
            
            // 准备并开始录制
            mediaRecorder.prepare();
            mediaRecorder.start();
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 停止录制音频
     * @return 录制的音频文件路径
     */
    public String stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                return audioFilePath;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    /**
     * 将音频文件转换为Base64字符串
     * @param filePath 音频文件路径
     * @return Base64编码的音频数据
     */
    public String audioFileToBase64(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            
            byte[] audioBytes = bos.toByteArray();
            fis.close();
            bos.close();
            
            // 转换为Base64
            return Base64.encodeToString(audioBytes, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 取消录制
     */
    public void cancelRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                
                // 删除临时文件
                if (audioFilePath != null) {
                    File file = new File(audioFilePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 检查是否正在录制
     * @return 是否正在录制
     */
    public boolean isRecording() {
        return mediaRecorder != null;
    }
}
