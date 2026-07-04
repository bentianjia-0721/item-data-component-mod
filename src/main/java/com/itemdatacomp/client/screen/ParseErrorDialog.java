package com.itemdatacomp.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * 命令解析错误提示对话框
 * 显示友好的错误信息给用户
 */
public class ParseErrorDialog extends ItemDataScreen {
    private final Screen parent;
    private final String errorMessage;
    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 180;

    public ParseErrorDialog(Screen parent, String errorMessage) {
        super(Text.literal("命令解析失败"));
        this.parent = parent;
        this.errorMessage = errorMessage != null ? errorMessage : "未知错误";
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 关闭按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("关闭"),
            button -> this.close()
        ).dimensions(
            centerX - 40,
            centerY + 50,
            80,
            20
        ).build());

        // 复制按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("复制错误"),
            button -> copyErrorToClipboard()
        ).dimensions(
            centerX - 120,
            centerY + 50,
            70,
            20
        ).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 背景
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int dialogX = centerX - DIALOG_WIDTH / 2;
        int dialogY = centerY - DIALOG_HEIGHT / 2;

        // 半透明背景
        context.fill(0, 0, this.width, this.height, 0x80000000);

        // 对话框背景
        context.fill(dialogX, dialogY, dialogX + DIALOG_WIDTH, dialogY + DIALOG_HEIGHT, 0xFF3F3F3F);

        // 边框
        context.fill(dialogX - 1, dialogY - 1, dialogX + DIALOG_WIDTH + 1, dialogY, 0xFFFFFFFF);
        context.fill(dialogX - 1, dialogY + DIALOG_HEIGHT, dialogX + DIALOG_WIDTH + 1, dialogY + DIALOG_HEIGHT + 1, 0xFFFFFFFF);
        context.fill(dialogX - 1, dialogY, dialogX, dialogY + DIALOG_HEIGHT, 0xFFFFFFFF);
        context.fill(dialogX + DIALOG_WIDTH, dialogY, dialogX + DIALOG_WIDTH + 1, dialogY + DIALOG_HEIGHT, 0xFFFFFFFF);

        // 标题
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal("命令解析失败"),
            centerX,
            dialogY + 15,
            0xFFFF5555
        );

        // 错误信息（可换行显示）
        String[] lines = wrapText(errorMessage, 38);
        int textY = dialogY + 40;
        int lineNum = 1;
        for (String line : lines) {
            // 行号前缀
            String linePrefix = "[" + lineNum + "] ";
            context.drawTextWithShadow(
                this.textRenderer,
                linePrefix + line,
                dialogX + 15,
                textY,
                0xFFCCCCCC
            );
            textY += 12;
            lineNum++;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * 文本换行处理
     */
    private String[] wrapText(String text, int maxCharsPerLine) {
        String[] words = text.split(" ");
        java.util.List<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if ((currentLine.length() + word.length() + 1) > maxCharsPerLine) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines.toArray(new String[0]);
    }

    private void copyErrorToClipboard() {
        this.client.keyboard.setClipboard("解析错误: " + errorMessage);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    /**
     * 静态方法显示错误对话框
     */
    public static void show(Screen parent, String error) {
        if (parent != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.setScreen(new ParseErrorDialog(parent, error));
            }
        }
    }
}
