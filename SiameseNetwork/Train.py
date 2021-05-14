import torchvision.datasets as dset
from SiameseNetwork.Dataset import SiameseNetworkDataset
import torchvision.transforms as transforms
from SiameseNetwork.Network import SiameseNetwork
from SiameseNetwork.ContrastiveLoss import ContrastiveLoss
from torch import optim
from torch.utils.data import DataLoader
import matplotlib.pyplot as plt
import torch
from torch.autograd import Variable



MODELNAME = 'trained_model' + '.pt'

training_dir = "../img/train/"
train_batch_size = 16
train_number_epochs = 100


def train():
    train_dataset_folder = dset.ImageFolder(root=training_dir)

    siamese_train_dataset = SiameseNetworkDataset(dataPath=train_dataset_folder,
                                            transform=transforms.Compose([transforms.Resize((100, 100)), transforms.ToTensor()]),
                                            should_invert=False)

    train_dataloader = DataLoader(siamese_train_dataset, shuffle=True, num_workers=0, batch_size=train_batch_size)

    net = SiameseNetwork().cuda()
    criterion = ContrastiveLoss()
    optimizer = optim.Adam(net.parameters(), lr=0.0005)

    counter = []
    loss_history = []
    iteration_number = 0

    for epoch in range(train_number_epochs):
        for i, data in enumerate(train_dataloader, 0):
            img0, img1, label = data
            img0, img1, label = Variable(img0).cuda(), Variable(img1).cuda(), Variable(label).cuda()
            output1,output2 = net(img0, img1)
            optimizer.zero_grad()
            loss_contrastive = criterion(output1, output2, label)
            loss_contrastive.backward()
            optimizer.step()
            if i % 10 == 0:
                print("Epoch number {}\n Current loss {}\n".format(epoch,loss_contrastive.item()))
                iteration_number += 10
                counter.append(iteration_number)
                loss_history.append(loss_contrastive.item())

    print('Training done.')
    torch.save(net.state_dict(), "./"+MODELNAME)
    plt.plot(counter, loss_history)
    plt.show()
