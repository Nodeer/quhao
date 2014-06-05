//
//  MessageInputView.m
//  quHaoIos
//
//  Created by sam on 14-6-4.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "MessageInputView.h"
#import "NSString+Message.h"

@interface MessageInputView()<UITextViewDelegate>

@property (nonatomic, weak, readwrite) MessageTextView *messageInputTextView;

@property (nonatomic, copy) NSString *inputedText;

@end

@implementation MessageInputView

- (void)dealloc{
    _messageInputTextView.delegate = nil;
    _messageInputTextView = nil;
    _faceSendButton = nil;
}

#pragma mark - Action

- (void)messageStyleButtonClicked:(UIButton *)sender {
    switch (sender.tag) {
        case 1:
        {
            //self.multiMediaSendButton.selected = NO;
            
            sender.selected = !sender.selected;
            if (sender.selected) {
                //NSLog(@"表情被点击");
                [self.messageInputTextView resignFirstResponder];
            }else{
                //NSLog(@"表情没被点击");
                [self.messageInputTextView becomeFirstResponder];
            }
            
            //            [UIView animateWithDuration:0.2 delay:0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
            //                self.messageInputTextView.hidden = NO;
            //            } completion:^(BOOL finished) {
            //
            //            }];
            
            if ([self.delegate respondsToSelector:@selector(didSendFaceAction:)]) {
                [self.delegate didSendFaceAction:sender.selected];
            }
        }
            break;
        case 2:
        {
        }
            break;
        default:
            break;
    }
}

#pragma mark - 添加控件
- (void)setupMessageInputViewBarWithStyle:(ZBMessageInputViewStyle )style{
    // 配置输入工具条的样式和布局
    
    // 水平间隔
    CGFloat horizontalPadding = 8;
    
    // 垂直间隔
    CGFloat verticalPadding = 5;
    
    // 按钮长,宽
    CGFloat buttonSize = [MessageInputView textViewLineHeight];
    
    // 发送表情
    self.faceSendButton = [self createButtonWithImage:[UIImage imageNamed:@"ToolViewEmotion_ios7"]
                                              HLImage:nil];
    [self.faceSendButton setImage:[UIImage imageNamed:@"ToolViewKeyboard_ios7"]
                         forState:UIControlStateSelected];
    [self.faceSendButton addTarget:self
                            action:@selector(messageStyleButtonClicked:)
                  forControlEvents:UIControlEventTouchUpInside];
    self.faceSendButton.tag = 1;
    self.faceSendButton.frame = CGRectMake(self.frame.size.width - 3*buttonSize- horizontalPadding -5,verticalPadding,buttonSize,buttonSize);
    [self addSubview:self.faceSendButton];

    // 初始化输入框
    MessageTextView *textView = [[MessageTextView alloc] initWithFrame:CGRectZero];
    textView.returnKeyType = UIReturnKeySend;
    textView.enablesReturnKeyAutomatically = YES; // UITextView内部判断send按钮是否可以用
    textView.placeHolder = @"发送消息";
    textView.delegate = self;
    [self addSubview:textView];
	self.messageInputTextView = textView;
    
    // 配置不同iOS SDK版本的样式
    switch (style)
    {
        case ZBMessageInputViewStyleQuasiphysical:
        {
            self.messageInputTextView.frame = CGRectMake(horizontalPadding  +5.0f,
                                                         3.0f,
                                                         CGRectGetWidth(self.bounds)- 3*buttonSize -2*horizontalPadding- 15.0f,
                                                         buttonSize);
            _messageInputTextView.backgroundColor = [UIColor whiteColor];
            
            break;
        }
        case ZBMessageInputViewStyleDefault:
        {
            self.messageInputTextView.frame = CGRectMake(horizontalPadding  +5.0f,4.5f,CGRectGetWidth(self.bounds)- 3*buttonSize -2*horizontalPadding- 15.0f,buttonSize);
            _messageInputTextView.backgroundColor = [UIColor clearColor];
            _messageInputTextView.layer.borderColor = [UIColor colorWithWhite:0.8f alpha:1.0f].CGColor;
            _messageInputTextView.layer.borderWidth = 0.65f;
            _messageInputTextView.layer.cornerRadius = 6.0f;
            
            break;
        }
        default:
            break;
    }
    
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setup];
    }
    return self;
}

#pragma mark - layout subViews UI
- (UIButton *)createButtonWithImage:(UIImage *)image HLImage:(UIImage *)hlImage {
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, [MessageInputView textViewLineHeight], [MessageInputView textViewLineHeight])];
    if (image)
        [button setBackgroundImage:image forState:UIControlStateNormal];
    if (hlImage)
        [button setBackgroundImage:hlImage forState:UIControlStateHighlighted];
    return button;
}
#pragma end

#pragma mark - Message input view

- (void)adjustTextViewHeightBy:(CGFloat)changeInHeight {
    // 动态改变自身的高度和输入框的高度
    CGRect prevFrame = self.messageInputTextView.frame;
    
    NSUInteger numLines = MAX([self.messageInputTextView numberOfLinesOfText],
                              [self.messageInputTextView.text numberOfLines]);
    
    self.messageInputTextView.frame = CGRectMake(prevFrame.origin.x,
                                                 prevFrame.origin.y,
                                                 prevFrame.size.width,
                                                 prevFrame.size.height + changeInHeight);
    
    
    self.messageInputTextView.contentInset = UIEdgeInsetsMake((numLines >= 6 ? 4.0f : 0.0f),
                                                              0.0f,
                                                              (numLines >= 6 ? 4.0f : 0.0f),
                                                              0.0f);
    
    // from iOS 7, the content size will be accurate only if the scrolling is enabled.
    self.messageInputTextView.scrollEnabled = YES;
    
    if (numLines >= 6) {
        CGPoint bottomOffset = CGPointMake(0.0f, self.messageInputTextView.contentSize.height - self.messageInputTextView.bounds.size.height);
        [self.messageInputTextView setContentOffset:bottomOffset animated:YES];
        [self.messageInputTextView scrollRangeToVisible:NSMakeRange(self.messageInputTextView.text.length - 2, 1)];
    }
}

+ (CGFloat)textViewLineHeight{
    return 36.0f ;// 字体大小为16
}

+ (CGFloat)maxHeight{
    return ([MessageInputView maxLines] + 1.0f) * [MessageInputView textViewLineHeight];
}

+ (CGFloat)maxLines{
    return ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPhone) ? 3.0f : 8.0f;
}
#pragma end

- (void)setup {
    // 配置自适应
    //self.autoresizesSubviews = YES;
    //self.autoresizingMask = (UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight);
    self.autoresizingMask = (UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin);
    self.opaque = YES;
    // 由于继承UIImageView，所以需要这个属性设置
    self.userInteractionEnabled = YES;
    
    if ([[[UIDevice currentDevice]systemVersion]floatValue]>=7 )
    {
        _messageInputViewStyle = ZBMessageInputViewStyleDefault;
        self.image = [[UIImage imageNamed:@"input-bar-flat"] resizableImageWithCapInsets:UIEdgeInsetsMake(2.0f, 0.0f, 0.0f, 0.0f)
                                                                            resizingMode:UIImageResizingModeStretch];
    }
    else
    {
        _messageInputViewStyle = ZBMessageInputViewStyleQuasiphysical;
        self.image = [[UIImage imageNamed:@"input-bar-background"] resizableImageWithCapInsets:UIEdgeInsetsMake(19.0f, 3.0f, 19.0f, 3.0f)
                                                                                  resizingMode:UIImageResizingModeStretch];
        
    }
    [self setupMessageInputViewBarWithStyle:_messageInputViewStyle];
}

#pragma mark - textViewDelegate
- (BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    
    if ([self.delegate respondsToSelector:@selector(inputTextViewWillBeginEditing:)])
    {
        [self.delegate inputTextViewWillBeginEditing:self.messageInputTextView];
    }
    self.faceSendButton.selected = NO;
    //self.multiMediaSendButton.selected = NO;
    
    return YES;
}

- (void)textViewDidChange:(UITextView *)textView{
    if ([self.delegate respondsToSelector:@selector(inputTextViewDidChange:)]) {
        [self.delegate inputTextViewDidChange:self.messageInputTextView];
    }
}

- (void)textViewDidBeginEditing:(UITextView *)textView{
    [textView becomeFirstResponder];
    
    if ([self.delegate respondsToSelector:@selector(inputTextViewDidBeginEditing:)]) {
        [self.delegate inputTextViewDidBeginEditing:self.messageInputTextView];
    }
}

- (void)textViewDidEndEditing:(UITextView *)textView{
    [textView resignFirstResponder];
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    if ([text isEqualToString:@"\n"]) {
        if ([self.delegate respondsToSelector:@selector(didSendTextAction:)]) {
            [self.delegate didSendTextAction:self.messageInputTextView];
        }
        return NO;
    }
    return YES;
}
#pragma end

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect
 {
 // Drawing code
 }
 */

@end

