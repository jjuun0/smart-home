from SiameseNetwork.Test import test

if __name__=="__main__":
    # train()
    test()

#
# def imshow(img, text=None, should_save=False):
#     npimg = img.numpy()
#     plt.axis("off")
#     if text:
#         plt.text(75, 8, text, style='italic', fontweight='bold',
#                  bbox={'facecolor': 'white', 'alpha': 0.8, 'pad': 10})
#
#     # PyTorch 는 이미지 데이터셋을 [Batch Size, Channel, Width, Height] 순서대로 저장
#     # -> matplotlib 로 출력하기 위해서는 각 이미지를 [Width, Height, Channel] 형태로 변경
#     plt.imshow(np.transpose(npimg, (1, 2, 0)))
#     plt.show()


# training_dir = "img/train/"
# testing_dir = "img/test/"
# train_batch_size = 8
# train_number_epochs = 100


# train_dataset_folder = dset.ImageFolder(root=training_dir)

'''
class SiameseNetwork(nn.Module):
    def __init__(self):
        super(SiameseNetwork, self).__init__()
        self.cnn1 = nn.Sequential(
            nn.ReflectionPad2d(1),
            nn.Conv2d(1, 4, kernel_size=3),
            nn.ReLU(inplace=True),
            nn.BatchNorm2d(4),

            nn.ReflectionPad2d(1),
            nn.Conv2d(4, 8, kernel_size=3),
            nn.ReLU(inplace=True),
            nn.BatchNorm2d(8),

            nn.ReflectionPad2d(1),
            nn.Conv2d(8, 8, kernel_size=3),
            nn.ReLU(inplace=True),
            nn.BatchNorm2d(8),

        )

        self.fc1 = nn.Sequential(
            nn.Linear(8 * 100 * 100, 500),
            nn.ReLU(inplace=True),

            nn.Linear(500, 500),
            nn.ReLU(inplace=True),

            nn.Linear(500, 5))

    def forward_once(self, x):
        output = self.cnn1(x)
        output = output.view(output.size()[0], -1)
        output = self.fc1(output)
        return output

    def forward(self, input1, input2):
        output1 = self.forward_once(input1)
        output2 = self.forward_once(input2)
        return output1, output2
'''

'''
class SiameseNetworkDataset(Dataset):
    def __init__(self, imageFolderDataset, transform=None, should_invert=True):
        self.imageFolderDataset = imageFolderDataset
        self.transform = transform
        self.should_invert = should_invert

    def __getitem__(self, index):
        img0_tuple = random.choice(self.imageFolderDataset.imgs)
        # we need to make sure approx 50% of images are in the same class
        should_get_same_class = random.randint(0, 1)
        if should_get_same_class:
            while True:
                # keep looping till the same class image is found
                img1_tuple = random.choice(self.imageFolderDataset.imgs)
                if img0_tuple[1] == img1_tuple[1]:
                    break
        else:
            while True:
                # keep looping till a different class image is found

                img1_tuple = random.choice(self.imageFolderDataset.imgs)
                if img0_tuple[1] != img1_tuple[1]:
                    break

        img0 = Image.open(img0_tuple[0])
        img1 = Image.open(img1_tuple[0])
        img0 = img0.convert("L")
        img1 = img1.convert("L")

        if self.should_invert:
            img0 = PIL.ImageOps.invert(img0)
            img1 = PIL.ImageOps.invert(img1)

        if self.transform is not None:
            img0 = self.transform(img0)
            img1 = self.transform(img1)

        return img0, img1, torch.from_numpy(np.array([int(img1_tuple[1] != img0_tuple[1])], dtype=np.float32))

    def __len__(self):
        return len(self.imageFolderDataset.imgs)
'''
# siamese_train_dataset = SiameseNetworkDataset(dataPath=train_dataset_folder,
#                                         transform=transforms.Compose([transforms.Resize((100, 100)), transforms.ToTensor()]),
#                                         should_invert=False)

'''
class ContrastiveLoss(torch.nn.Module):
    """
    Contrastive loss function.
    Based on: http://yann.lecun.com/exdb/publis/pdf/hadsell-chopra-lecun-06.pdf
    """

    def __init__(self, margin=2.0):
        super(ContrastiveLoss, self).__init__()
        self.margin = margin

    def forward(self, output1, output2, label):
        euclidean_distance = F.pairwise_distance(output1, output2, keepdim = True)
        loss_contrastive = torch.mean((1-label) * torch.pow(euclidean_distance, 2) +
                                      (label) * torch.pow(torch.clamp(self.margin - euclidean_distance, min=0.0), 2))

        return loss_contrastive
'''

# train_dataloader = DataLoader(siamese_train_dataset, shuffle=True, num_workers=0, batch_size=train_batch_size)
#
# net = SiameseNetwork().cuda()
# criterion = ContrastiveLoss()
# optimizer = optim.Adam(net.parameters(), lr=0.0005)
#
# counter = []
# loss_history = []
# iteration_number = 0
#
# for epoch in range(train_number_epochs):
#     for i, data in enumerate(train_dataloader, 0):
#         img0, img1, label = data
#         img0, img1, label = img0.cuda(), img1.cuda(), label.cuda()
#         optimizer.zero_grad()
#         output1,output2 = net(img0, img1)
#         loss_contrastive = criterion(output1, output2, label)
#         loss_contrastive.backward()
#         optimizer.step()
#         if i % 10 == 0:
#             print("Epoch number {}\n Current loss {}\n".format(epoch,loss_contrastive.item()))
#             iteration_number += 10
#             counter.append(iteration_number)
#             loss_history.append(loss_contrastive.item())
#
# # show_plot(counter, loss_history)
# plt.plot(counter, loss_history)
# plt.show()

# testing_dir= "img/test/"
# test_dataset_folder = dset.ImageFolder(root=testing_dir)
# siamese_test_dataset = SiameseNetworkDataset(dataPath=test_dataset_folder,
#                                         transform=transforms.Compose([transforms.Resize((100, 100)),
#                                                                       transforms.ToTensor()]),
#                                         should_invert=False)
#
#
# test_dataloader = DataLoader(siamese_test_dataset, num_workers=0, batch_size=1, shuffle=True)
# dataiter = iter(test_dataloader)
# x0, _, _ = next(dataiter)
#
# for i in range(10):
#     _, x1, label2 = next(dataiter)
#     concatenated = torch.cat((x0, x1), 0)
#
#     output1, output2 = net(Variable(x0).cuda(), Variable(x1).cuda())
#     euclidean_distance = F.pairwise_distance(output1, output2)
#     imshow(torchvision.utils.make_grid(concatenated), 'Dissimilarity: {:.2f}'.format(euclidean_distance.item()))